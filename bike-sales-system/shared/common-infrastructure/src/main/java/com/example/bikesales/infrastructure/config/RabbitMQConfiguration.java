package com.example.bikesales.infrastructure.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.TopicExchange; // Added import
import org.springframework.context.annotation.Bean;    // Added import
import org.springframework.amqp.core.Queue;             // Added import
import org.springframework.amqp.core.QueueBuilder;      // Added import
import org.springframework.amqp.core.Binding;           // Added import
import org.springframework.amqp.core.BindingBuilder;    // Added import
import org.springframework.amqp.core.FanoutExchange;    // Added import
// Other imports for Queue, Exchange, Binding, Bean will be added in later steps

@Configuration
@EnableRabbit
public class RabbitMQConfiguration {

    @Bean
    public TopicExchange orderExchange() {
        // String name, boolean durable, boolean autoDelete
        return new TopicExchange("order.exchange", true, false);
    }

    @Bean
    public Queue checkoutQueue() {
        return QueueBuilder.durable("checkout.processing.queue")
                .withArgument("x-dead-letter-exchange", "dlx.exchange") // Standard DLX argument
                // .withArgument("x-dead-letter-routing-key", "dlx.checkout.key") // Optional: specific DL routing key
                .build();
    }

    @Bean
    public Queue paymentQueue() {
        // As per issue: durable, with x-message-ttl
        return QueueBuilder.durable("payment.processing.queue")
                .withArgument("x-message-ttl", 30000) // Time-to-live for messages in milliseconds
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        // Durable queue for notifications, using QueueBuilder for consistency
        return QueueBuilder.durable("notification.queue").build();
    }

    @Bean
    public Binding bindingOrderCreatedToPaymentQueue(Queue paymentQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(paymentQueue)
                .to(orderExchange)
                .with("order.created"); // Assuming 'order.created' is the routing key for OrderCreatedEvent
    }

    // Dead Letter Exchange (DLX)
    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange("dlx.exchange"); // Standard DLX name
    }

    // Dead Letter Queue (DLQ)
    @Bean
    public Queue deadLetterQueue() {
        // This queue will store messages that are dead-lettered from other queues
        return QueueBuilder.durable("dead.letter.queue").build();
    }

    // Binding for the DLQ to the DLX
    @Bean
    public Binding bindingDeadLetterQueueToDlx(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange);
    }

    // Bean definitions for exchanges, queues, and bindings will be added here.
    // This class serves as a central place for RabbitMQ related bean definitions.
    // For example:
    //
    // @Bean
    // public Queue myQueue() {
    //     return new Queue("myQueueName", true); // durable queue
    // }
    //
    // @Bean
    // public TopicExchange myExchange() {
    //     return new TopicExchange("myExchangeName");
    // }
    //
    // @Bean
    // public Binding myBinding(Queue myQueue, TopicExchange myExchange) {
    //     return BindingBuilder.bind(myQueue).to(myExchange).with("my.routing.key");
    // }
    //
    // // For JSON message conversion (if not using default or if specific ObjectMapper needed)
    // @Bean
    // public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
    //     return new Jackson2JsonMessageConverter(objectMapper);
    // }
    //
    // @Bean
    // public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    //     RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    //     rabbitTemplate.setMessageConverter(messageConverter);
    //     return rabbitTemplate;
    // }
}
