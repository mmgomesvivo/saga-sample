package com.example.bikesales.infrastructure.config;

import com.example.bikesales.infrastructure.saga.JdbcSagaStateRepository;
import com.example.bikesales.saga.SagaOrchestrator;
import com.example.bikesales.saga.SagaStateRepository;
// Import RabbitTemplate even if commented in method signature for completeness,
// or if it might be uncommented later.
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SagaConfiguration {

    @Bean
    public SagaStateRepository sagaStateRepository(JdbcTemplate jdbcTemplate) {
        // Assuming ObjectMapper is not strictly needed by JdbcSagaStateRepository
        // for its current placeholder implementation. If it were, it would be injected here too.
        return new JdbcSagaStateRepository(jdbcTemplate);
    }

    @Bean
    public SagaOrchestrator sagaOrchestrator(
            // RabbitTemplate rabbitTemplate, // Commented out to match SagaOrchestrator constructor
            SagaStateRepository sagaStateRepository) {
        // Ensure SagaOrchestrator's constructor matches this call.
        // If RabbitTemplate was a required (though unused) param in constructor,
        // we might pass null or a mock if context allows.
        // But current SagaOrchestrator has it commented out.
        return new SagaOrchestrator(/* rabbitTemplate, */ sagaStateRepository);
    }
}
