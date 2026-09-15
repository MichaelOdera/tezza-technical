package com.tezza.notification.config;

import com.tezza.contracts.Messaging;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    DirectExchange eventsExchange() {
        return new DirectExchange(Messaging.EXCHANGE);
    }

    @Bean
    Queue notificationQueue() {
        return new Queue(Messaging.NOTIFICATIONS_QUEUE, true);
    }

    @Bean
    Binding loanCreatedBinding(Queue notificationQueue, DirectExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(eventsExchange)
                .with(Messaging.LOAN_CREATED);
    }

    @Bean
    Binding repaymentBinding(Queue notificationQueue, DirectExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(eventsExchange)
                .with(Messaging.REPAYMENT_RECEIVED);
    }

    @Bean
    Binding overdueBinding(Queue notificationQueue, DirectExchange eventsExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(eventsExchange)
                .with(Messaging.LOAN_OVERDUE);
    }

    @Bean
    JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
