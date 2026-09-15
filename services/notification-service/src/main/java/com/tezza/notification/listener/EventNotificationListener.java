package com.tezza.notification.listener;


import com.tezza.notification.channel.ChannelDeliveryStrategy;
import com.tezza.notification.model.Notification;
import com.tezza.notification.repository.NotificationRepository;
import com.tezza.notification.templates.NotificationTemplateEngine;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventNotificationListener {

    private final NotificationTemplateEngine templateEngine;
    private final NotificationRuleEvaluator ruleEvaluator;
    private final NotificationRepository notificationRepository;
    private final Map<Notification.NotificationChannel, ChannelDeliveryStrategy> strategyMap;

    public EventNotificationListener(NotificationTemplateEngine templateEngine,
                                     NotificationRuleEvaluator ruleEvaluator,
                                     NotificationRepository notificationRepository,
                                     List<ChannelDeliveryStrategy> deliveryStrategies) {
        this.templateEngine = templateEngine;
        this.ruleEvaluator = ruleEvaluator;
        this.notificationRepository = notificationRepository;
        this.strategyMap = deliveryStrategies.stream()
                .collect(Collectors.toMap(ChannelDeliveryStrategy::getSupportedChannel, Function.identity()));
    }

    /**
     * Listens for Event payloads dispatched from the Loan platform
     */
    @RabbitListener(queues = "${tezza.notification.queue-name:loan-events-queue}")
    public void handlePlatformEvent(Map<String, Object> eventPayload) {
        String eventType = (String) eventPayload.get("eventType");
        UUID customerId = UUID.fromString((String) eventPayload.get("customerId"));
        UUID loanId = eventPayload.get("loanId") != null ? UUID.fromString((String) eventPayload.get("loanId")) : null;
        
        @SuppressWarnings("unchecked")
        Map<String, String> contextVariables = (Map<String, String>) eventPayload.get("variables");

        // 1. Calculate dynamic message layout strings and rule scopes
        String formattedMessage = templateEngine.interpolate(eventType, contextVariables);
        Set<Notification.NotificationChannel> enabledChannels = ruleEvaluator.determineTargetChannels(eventType, customerId.toString());

        // 2. Build tracking tracking log context block entity
        Notification trackingRecord = new Notification(customerId, loanId, eventType, formattedMessage, enabledChannels);

        // 3. Persist log inside bounded elastic thread pool, then execute concurrent async pipeline delivery
        Mono.fromCallable(() -> notificationRepository.save(trackingRecord))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(savedLog -> Flux.fromIterable(enabledChannels)
                        .flatMap(channel -> {
                            ChannelDeliveryStrategy strategy = strategyMap.get(channel);
                            if (strategy != null) {
                                return strategy.send(customerId.toString(), formattedMessage);
                            }
                            return Mono.empty();
                        }).then()
                ).subscribe(); // Detached runtime execution context safe for 1000+ RPS architectures
    }
}
