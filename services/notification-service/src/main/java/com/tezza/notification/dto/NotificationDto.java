package com.tezza.notification.dto;

import com.tezza.notification.model.Notification;
import java.time.Instant;
import java.util.UUID;

public record NotificationDto(UUID id, UUID customerId, UUID loanId, String eventType, String channel, String message, Instant createdAt) {
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getCustomerId(), notification.getLoanId(), notification.getEventType(),
                notification.getChannels().toString(), notification.getMessage(), notification.getCreatedAt());
    }
}
