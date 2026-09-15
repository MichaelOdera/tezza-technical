package com.tezza.gateway.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRabbitConfiguration {
    @Bean
    DirectExchange gatewayEventsExchange() {
        return new DirectExchange("tezza.gateway.events");
    }

    @Bean
    JacksonJsonMessageConverter gatewayJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}