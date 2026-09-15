package com.tezza.notification.channel;


import com.tezza.notification.model.Notification;
import reactor.core.publisher.Mono;

public interface ChannelDeliveryStrategy {
    Notification.NotificationChannel getSupportedChannel();
    Mono<Void> send(String customerId, String message);
}
