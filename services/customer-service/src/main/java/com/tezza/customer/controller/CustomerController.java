package com.tezza.customer.controller;

import com.tezza.customer.model.Customer;
import com.tezza.customer.messaging.CustomerEventPublisher;
import com.tezza.customer.repository.CustomerRepository;
import com.tezza.customer.dto.CustomerRequest;
import com.tezza.customer.dto.CreateCustomerDto;
import com.tezza.customer.dto.CustomerDto;
import com.tezza.customer.dto.ReserveCreditDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer profiles and credit limits")
public class CustomerController {
    private final CustomerRepository repository;
    private final CustomerEventPublisher publisher;

    public CustomerController(CustomerRepository repository, CustomerEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a customer")
    public Mono<CustomerDto> create(@Valid @RequestBody CreateCustomerDto request) {
        return Mono.fromCallable(() -> {
            Customer customer = repository.save(new Customer(new CustomerRequest(
                    request.firstName(), request.lastName(), request.email(), request.phone(), request.loanLimit())));
            publisher.publish(customer);
            return CustomerDto.from(customer);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping
    @Operation(summary = "List customers")
    public Flux<CustomerDto> all() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(CustomerDto::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer")
    public Mono<CustomerDto> get(@PathVariable UUID id) {
        return Mono.fromCallable(() -> CustomerDto.from(find(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve customer credit")
    public Mono<CustomerDto> reserve(@PathVariable UUID id,
                                     @Valid @RequestBody ReserveCreditDto request) {
        return Mono.fromCallable(() -> {
            Customer customer = find(id);
            if (customer.getAvailableLimit().compareTo(request.amount()) < 0) {
                throw new IllegalArgumentException("Loan limit exceeded");
            }
            customer.reserve(request.amount());
            return CustomerDto.from(repository.save(customer));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Customer find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }
}
