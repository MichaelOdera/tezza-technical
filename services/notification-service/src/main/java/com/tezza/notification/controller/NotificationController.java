package com.tezza.notification.controller;

import com.tezza.notification.repository.NotificationRepository;
import com.tezza.notification.dto.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Persisted customer notification events")
public class NotificationController {
    private final NotificationRepository repository;

    public NotificationController(NotificationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List customer notifications")
    public Flux<NotificationDto> customer(@PathVariable UUID customerId) {
        return Mono.fromCallable(() -> repository.findByCustomerIdOrderByCreatedAtDesc(customerId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(NotificationDto::from);
    }
}
