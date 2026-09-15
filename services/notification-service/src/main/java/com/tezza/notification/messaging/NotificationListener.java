package com.tezza.notification.messaging;

import com.tezza.contracts.LoanCreatedEvent;
import com.tezza.contracts.LoanOverdueEvent;
import com.tezza.contracts.Messaging;
import com.tezza.contracts.RepaymentReceivedEvent;
import com.tezza.notification.model.Notification;
import com.tezza.notification.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RabbitListener(queues = Messaging.NOTIFICATIONS_QUEUE)
public class NotificationListener {
    private final NotificationRepository repository;

    public NotificationListener(NotificationRepository repository) {
        this.repository = repository;
    }

    @RabbitHandler
    @Transactional
    public void loanCreated(LoanCreatedEvent event) {
        repository.save(new Notification(
                event.customerId(),
                event.loanId(),
                Messaging.LOAN_CREATED,
                "Loan " + event.loanId() + " was created.",
                Set.of(Notification.NotificationChannel.valueOf("EMAIL"))));
    }

    @RabbitHandler
    @Transactional
    public void repaymentReceived(RepaymentReceivedEvent event) {
        repository.save(new Notification(
                event.customerId(),
                event.loanId(),
                Messaging.REPAYMENT_RECEIVED,
                "Repayment received: " + event.amount(),
                Set.of(Notification.NotificationChannel.valueOf("EMAIL"))));
    }

    @RabbitHandler
    @Transactional
    public void overdue(LoanOverdueEvent event) {
        repository.save(new Notification(
                event.customerId(),
                event.loanId(),
                Messaging.LOAN_OVERDUE,
                "Loan " + event.loanId() + " is overdue.",
                Set.of(Notification.NotificationChannel.valueOf("SMS"))));
    }
}
