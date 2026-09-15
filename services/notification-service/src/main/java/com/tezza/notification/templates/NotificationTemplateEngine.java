package com.tezza.notification.templates;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationTemplateEngine {

    private final Map<String, String> templates = new ConcurrentHashMap<>();

    public NotificationTemplateEngine() {
        // Seeding baseline variable layout states
        templates.put("LOAN_CREATED", "Hello ${customerName}, your loan of ${amount} has been successfully disbursed! Due date: ${dueDate}.");
        templates.put("REPAYMENT_ACK", "Hi ${customerName}, thank you for your payment of ${amount}. Your remaining balance is ${balance}.");
        templates.put("OVERDUE_NOTICE", "URGENT: ${customerName}, your loan is overdue by ${daysLate} days. Late fees of ${lateFee} applied.");
    }

    public String interpolate(String eventType, Map<String, String> variables) {
        String template = templates.getOrDefault(eventType, "Notification Alert: ${message}");
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
        }
        return template;
    }

    public void registerOrUpdateTemplate(String eventType, String templateContent) {
        this.templates.put(eventType, templateContent);
    }
}
