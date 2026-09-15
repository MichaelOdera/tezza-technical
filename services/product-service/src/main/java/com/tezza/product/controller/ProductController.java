package com.tezza.product.controller;

import com.tezza.product.model.Product;
import com.tezza.product.messaging.ProductEventPublisher;
import com.tezza.product.repository.ProductRepository;
import com.tezza.product.dto.ProductRequest;
import com.tezza.product.dto.CreateProductDto;
import com.tezza.product.dto.ProductDto;
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
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Loan product configuration and fee policies")
public class ProductController {
    private final ProductRepository repository;
    private final ProductEventPublisher publisher;

    public ProductController(ProductRepository repository, ProductEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a loan product")
    public Mono<ProductDto> create(@Valid @RequestBody CreateProductDto request) {
        return Mono.fromCallable(() -> {
            ProductRequest productRequest = new ProductRequest(
                    request.code(), request.name(), request.tenureValue(), request.tenureUnit(),
                    request.serviceFeeType(), request.serviceFeeAmount(), request.dailyFee(),
                    request.lateFeeAmount(), request.lateFeeTriggerDays());

            if (repository.existsByCode(request.code())) {
                throw new IllegalArgumentException("Product code already exists");
            }

            Product product = repository.save(new Product(productRequest));
            publisher.publish(product);
            return ProductDto.from(product);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping
    @Operation(summary = "List loan products")
    public Flux<ProductDto> all() {
        return Mono.fromCallable(repository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(ProductDto::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a loan product")
    public Mono<ProductDto> get(@PathVariable UUID id) {
        return Mono.fromCallable(() -> repository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found")))
                .subscribeOn(Schedulers.boundedElastic())
                .map(ProductDto::from);
    }
}
