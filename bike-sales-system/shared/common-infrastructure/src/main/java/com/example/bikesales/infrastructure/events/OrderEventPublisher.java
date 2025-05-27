package com.example.bikesales.infrastructure.events;

import com.example.bikesales.domain.events.DomainEventPublisher;
import com.example.bikesales.domain.events.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher implements DomainEventPublisher<OrderCreatedEvent> {

    private final RabbitTemplate rabbitTemplate;
    // Hardcoding exchange and routing key as per instruction's direct approach,
    // rather than configurable fields from example.
    private static final String EXCHANGE_NAME = "order.exchange";
    private static final String ROUTING_KEY = "order.created";

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
        // Instruction: Call rabbitTemplate.convertAndSend("order.exchange", "order.created", event);
        // Instruction: Add a log statement: System.out.println("OrderEventPublisher: Published OrderCreatedEvent for Order ID: " + event.orderId().value());

        if (event == null) {
            // Adding a null check as good practice, though not explicitly in instruction's direct path
            System.err.println("OrderEventPublisher: Attempted to publish a null event. Skipping.");
            return;
        }
        
        // Using constants for exchange and routing key
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
        
        // Log statement as per instruction
        System.out.println("OrderEventPublisher: Published OrderCreatedEvent for Order ID: " + event.orderId().value());
        
        // The example includes more verbose logging and a try-catch block.
        // Sticking to the simpler instruction here.
    }
}
