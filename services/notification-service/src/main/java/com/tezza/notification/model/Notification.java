package com.tezza.notification.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    private UUID loanId;

    @Column(nullable = false)
    private String eventType;

    // Element collection stores channels dynamically in a nested join table without bloating rows
    @ElementCollection(targetClass = NotificationChannel.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "notification_channels", joinColumns = @JoinColumn(name = "notification_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private Set<NotificationChannel> channels = new HashSet<>();

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public enum NotificationChannel {
        EMAIL, SMS, PUSH
    }

    protected Notification() {
        // Required by JPA Spec
    }

    /**
     * Modernized constructor supporting multi-channel delivery configurations
     */
    public Notification(UUID customerId, UUID loanId, String eventType, String message, Set<NotificationChannel> targetChannels) {
        this.customerId = customerId;
        this.loanId = loanId;
        this.eventType = eventType;
        this.message = message;
        this.createdAt = Instant.now();
        if (targetChannels != null && !targetChannels.isEmpty()) {
            this.channels.addAll(targetChannels);
        } else {
            // High availability safety default if no channels are explicitly mapped
            this.channels.add(NotificationChannel.EMAIL); 
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public UUID getLoanId() { return loanId; }
    public String getEventType() { return eventType; }
    public Set<NotificationChannel> getChannels() { return channels; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
