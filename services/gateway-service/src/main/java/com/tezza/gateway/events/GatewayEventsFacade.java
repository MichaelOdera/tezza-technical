package com.tezza.gateway.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class GatewayEventsFacade {
    private static final String EXCHANGE = "tezza.gateway.events";
    private final RabbitTemplate rabbitTemplate;

    public GatewayEventsFacade(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void authenticationRejected(String path, String reason) {
        publish("tezza.gateway.authentication.rejected", Map.of("path", path, "reason", reason));
    }

    public void rateLimitExceeded(String client, String path) {
        publish("tezza.gateway.rate-limit.exceeded", Map.of("client", client, "path", path));
    }

    public void routeForwarded(String routeId, String path, int status) {
        publish("tezza.gateway.route.forwarded", Map.of("routeId", routeId, "path", path, "status", status));
    }

    private void publish(String routingKey, Map<String, Object> details) {
        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, Map.of(
                "event", routingKey,
                "occurredAt", Instant.now().toString(),
                "details", details));
    }
}