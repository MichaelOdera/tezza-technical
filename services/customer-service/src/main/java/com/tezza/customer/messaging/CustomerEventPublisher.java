package com.tezza.customer.messaging;

import com.tezza.contracts.CustomerCreatedEvent;
import com.tezza.contracts.Messaging;
import com.tezza.customer.model.Customer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public CustomerEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(Customer customer) {
        rabbitTemplate.convertAndSend(
                Messaging.EXCHANGE,
                Messaging.CUSTOMER_CREATED,
                new CustomerCreatedEvent(
                        customer.getId(),
                        customer.getEmail(),
                        customer.getPhone()));
    }
}
