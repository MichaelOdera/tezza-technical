package com.tezza.customer.config;

import com.tezza.contracts.Messaging;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    DirectExchange eventsExchange() { return new DirectExchange(Messaging.EXCHANGE); }

    @Bean
    JacksonJsonMessageConverter jsonMessageConverter() { return new JacksonJsonMessageConverter(); }
}
