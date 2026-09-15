package com.tezza.loan.controller;

import com.tezza.loan.model.Installment;
import com.tezza.loan.dto.LoanRequest;
import com.tezza.loan.application.LoanService;
import com.tezza.loan.dto.BillingDateDto;
import com.tezza.loan.dto.CreateLoanDto;
import com.tezza.loan.dto.LoanDto;
import com.tezza.loan.dto.RepaymentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "Loan disbursement, repayments, installments, and lifecycle processing")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Disburse a loan")
    public Mono<LoanDto> create(@Valid @RequestBody CreateLoanDto request) {
        LoanRequest loanRequest = new LoanRequest(
                request.customerId(), request.productId(), request.principal(),
                request.structure(), request.installmentCount(), request.idempotencyKey());
        return service.create(loanRequest)
                .subscribeOn(Schedulers.boundedElastic())
                .map(LoanDto::from);
    }

    @GetMapping
    @Operation(summary = "List loans")
    public Flux<LoanDto> all() {
        return Mono.fromCallable(service::all)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(LoanDto::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a loan")
    public Mono<LoanDto> get(@PathVariable UUID id) {
        return Mono.fromCallable(() -> service.get(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(LoanDto::from);
    }

    @GetMapping("/{id}/installments")
    @Operation(summary = "Get loan installments")
    public Flux<Installment> installments(@PathVariable UUID id) {
        return Mono.fromCallable(() -> service.installments(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    @PatchMapping("/{id}/billing-date")
    @Operation(summary = "Set a consolidated billing date")
    public Mono<LoanDto> billingDate(@PathVariable UUID id,
                                     @Valid @RequestBody BillingDateDto request) {
        return Mono.fromCallable(() -> service.consolidateBilling(id, request.dueDate()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(LoanDto::from);
    }

    @PostMapping("/{id}/repayments")
    @Operation(summary = "Record a repayment")
    public Mono<LoanDto> repay(@PathVariable UUID id,
                               @Valid @RequestBody RepaymentDto request) {
        return Mono.fromCallable(() -> service.repay(id, request.amount()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(LoanDto::from);
    }

    @PostMapping("/sweep")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Run the overdue loan sweep")
    public Mono<Void> sweep() {
        return Mono.fromRunnable(service::sweep)
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
