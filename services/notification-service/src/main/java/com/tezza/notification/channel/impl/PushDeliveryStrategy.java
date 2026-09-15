package com.tezza.notification.channel.impl;

import com.tezza.notification.channel.ChannelDeliveryStrategy;
import com.tezza.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PushDeliveryStrategy implements ChannelDeliveryStrategy {
    private static final Logger log = LoggerFactory.getLogger(PushDeliveryStrategy.class);

    @Override
    public Notification.NotificationChannel getSupportedChannel() { return Notification.NotificationChannel.PUSH; }

    @Override
    public Mono<Void> send(String customerId, String message) {
        return Mono.fromRunnable(() -> log.info("Firing Firebase Cloud PUSH vector notification to device profile ID [{}]: {}", customerId, message)).then();
    }
}
