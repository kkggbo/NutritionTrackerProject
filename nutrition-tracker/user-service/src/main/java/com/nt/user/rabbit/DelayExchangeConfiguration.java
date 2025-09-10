package com.nt.user.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

@Configuration
public class DelayExchangeConfiguration {
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange("challenge-delay-exchange");
    }

    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable("challenge-delay-queue").deadLetterExchange("challenge-dlx-exchange").build();
    }

    @Bean
    public Binding delayExchangeBinding(Queue delayQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with("challenge.delay.routingKey");
    }
}
