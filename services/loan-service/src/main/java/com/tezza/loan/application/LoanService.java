package com.tezza.loan.application;

import com.tezza.loan.config.LoanServiceProperties;
import com.tezza.loan.dto.LoanRequest;
import com.tezza.loan.messaging.LoanEventPublisher;
import com.tezza.loan.model.Installment;
import com.tezza.loan.model.Loan;
import com.tezza.loan.repository.InstallmentRepository;
import com.tezza.loan.repository.LoanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;


import org.springframework.http.HttpStatusCode;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
public class LoanService {
    private final LoanRepository loans;
    private final InstallmentRepository installments;
    private final LoanEventPublisher events;
    private final WebClient http;
    private final LoanServiceProperties properties;
    private final TransactionTemplate transactionTemplate; // Used to isolate the DB transaction phase

    public LoanService(LoanRepository loans,
                       InstallmentRepository installments,
                       LoanEventPublisher events,
                       LoanServiceProperties properties,
                       WebClient.Builder webClientBuilder,
                       TransactionTemplate transactionTemplate) {
        this.loans = loans;
        this.installments = installments;
        this.events = events;
        this.properties = properties;
        this.http = webClientBuilder.build();
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * Completely non-blocking reactive entrypoint.
     * Orchestrates remote network network traffic concurrently before committing DB resources.
     */
    public Mono<Loan> create(LoanRequest request) {
        // 1. Safe Idempotency Check (Executed on a JDBC-friendly bounded elastic scheduler)
        return Mono.fromCallable(() -> loans.findByIdempotencyKey(request.idempotencyKey()).orElse(null))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingLoan -> {
                    if (existingLoan != null) {
                        return Mono.just(existingLoan);
                    }

                    // 2. Trigger concurrent downstream microservice I/O pipelines
                    Mono<ProductSnapshot> productMono = http.get()
                            .uri(properties.getProductUrl() + "/api/products/" + request.productId())
                            .retrieve()
                            .bodyToMono(ProductSnapshot.class);

                    Mono<Void> customerReserveMono = http.post()
                            .uri(properties.getCustomerUrl() + "/api/customers/" + request.customerId() + "/reserve")
                            .bodyValue(Map.of("amount", request.principal()))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, clientResponse ->
                                    Mono.error(new IllegalArgumentException("Credit limit reservation failed"))
                            )
                            .toBodilessEntity()
                            .then();

                    // 3. Zip network calls to execute in parallel, then save inside a short-lived transaction block
                    return Mono.zip(productMono, customerReserveMono)
                            .flatMap(tuple -> Mono.fromCallable(() ->
                                    transactionTemplate.execute(status -> executeSavePipeline(request, tuple.getT1()))
                            ).subscribeOn(Schedulers.boundedElastic()));
                });
    }

    private Loan executeSavePipeline(LoanRequest request, ProductSnapshot product) {
        LocalDate originationDate = LocalDate.now();
        LocalDate dueDate = "DAYS".equals(product.tenureUnit())
                ? originationDate.plusDays(product.tenureValue())
                : originationDate.plusMonths(product.tenureValue());

        BigDecimal fee = calculateServiceFee(request.principal(), product);

        Loan loan = loans.save(new Loan(
                request, dueDate, fee, product.lateFeeAmount(), product.lateFeeTriggerDays()));

        createInstallments(request, loan, originationDate);
        events.loanCreated(loan);
        return loan;
    }

    @Transactional
    public Loan repay(UUID id, BigDecimal amount) {
        Loan loan = get(id);
        if (amount.compareTo(loan.getOutstandingBalance()) > 0) {
            throw new IllegalArgumentException("Repayment exceeds outstanding balance");
        }

        BigDecimal remaining = amount;
        for (Installment item : installments.findByLoanIdOrderByInstallmentNumber(id)) {
            BigDecimal allocation = remaining.min(item.remaining());
            item.apply(allocation);
            remaining = remaining.subtract(allocation);
            if (remaining.signum() == 0) {
                break;
            }
        }

        loan.repay(amount);
        Loan savedLoan = loans.save(loan);
        events.repayment(savedLoan, amount);
        return savedLoan;
    }

    @Scheduled(fixedDelayString = "#{@loanServiceProperties.getSweepFixedDelayMs()}")
    @Transactional
    public void sweep() {
        // Stream or chunk this query if the row footprint grows significantly under high load
        for (Loan loan : loans.findByStateAndDueDateBefore("OPEN", LocalDate.now())) {
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
            if (daysLate >= loan.getLateFeeTriggerDays() && loan.getLateFeeAmount().signum() > 0) {
                loan.addFee(loan.getLateFeeAmount());
            }
            loan.overdue();
            Loan savedLoan = loans.save(loan);
            events.overdue(savedLoan);
        }
    }

    public Loan get(UUID id) {
        return loans.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
    }

    public List<Loan> all() {
        return loans.findAll();
    }

    public List<Installment> installments(UUID id) {
        get(id);
        return installments.findByLoanIdOrderByInstallmentNumber(id);
    }

    @Transactional
    public Loan consolidateBilling(UUID id, LocalDate dueDate) {
        Loan loan = get(id);
        loan.setConsolidatedDueDate(dueDate);
        return loans.save(loan);
    }

    private BigDecimal calculateServiceFee(BigDecimal principal, ProductSnapshot product) {
        if ("PERCENTAGE".equals(product.serviceFeeType())) {
            return principal.multiply(product.serviceFeeAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return product.serviceFeeAmount();
    }

    private void createInstallments(LoanRequest request, Loan loan, LocalDate originationDate) {
        if (!"INSTALLMENTS".equals(request.structure())) {
            return;
        }

        BigDecimal amount = loan.getOutstandingBalance()
                .divide(BigDecimal.valueOf(request.installmentCount()), 2, RoundingMode.HALF_UP);
        for (int number = 1; number <= request.installmentCount(); number++) {
            BigDecimal installmentAmount = number == request.installmentCount()
                    ? loan.getOutstandingBalance().subtract(amount.multiply(BigDecimal.valueOf(number - 1L)))
                    : amount;
            installments.save(new Installment(
                    loan.getId(), number, installmentAmount, originationDate.plusMonths(number)));
        }
    }

    public record ProductSnapshot(int tenureValue,
                                  String tenureUnit,
                                  String serviceFeeType,
                                  BigDecimal serviceFeeAmount,
                                  BigDecimal lateFeeAmount,
                                  int lateFeeTriggerDays) {
    }
}
