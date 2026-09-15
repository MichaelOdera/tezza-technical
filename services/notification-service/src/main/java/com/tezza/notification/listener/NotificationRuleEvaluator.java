package com.tezza.notification.listener;

import com.tezza.notification.model.Notification;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class NotificationRuleEvaluator {

    /**
     * Resolves target channels combining product configurations, user options, and event severity.
     */
    public Set<Notification.NotificationChannel> determineTargetChannels(String eventType, String customerId) {
        // High Availability Rule: Overdue triggers all channels regardless of opt-outs
        if ("OVERDUE_NOTICE".equalsIgnoreCase(eventType)) {
            return Set.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.SMS, Notification.NotificationChannel.PUSH);
        }
        
        // Defaults to Email and App Push for standard account lifecycle milestones
        return Set.of(Notification.NotificationChannel.EMAIL, Notification.NotificationChannel.PUSH);
    }
}
