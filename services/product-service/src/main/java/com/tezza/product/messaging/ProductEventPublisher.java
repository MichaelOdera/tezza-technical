package com.tezza.product.messaging;

import com.tezza.contracts.Messaging;
import com.tezza.contracts.ProductCreatedEvent;
import com.tezza.product.model.Product;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ProductEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(Product product) {
        rabbitTemplate.convertAndSend(
                Messaging.EXCHANGE,
                Messaging.PRODUCT_CREATED,
                new ProductCreatedEvent(
                        product.getId(),
                        product.getCode(),
                        product.getName(),
                        product.getServiceFeeAmount()));
    }
}
