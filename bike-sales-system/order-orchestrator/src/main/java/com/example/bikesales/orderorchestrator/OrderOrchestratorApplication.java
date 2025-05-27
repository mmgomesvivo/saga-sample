package com.example.bikesales.orderorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// If shared configurations (like RabbitMQConfiguration from common-infrastructure)
// or components from other base packages (like checkout.domain for BikeOrderItem)
// are not automatically picked up due to module structure and dependencies,
// scanBasePackages might be needed.
// e.g., @SpringBootApplication(scanBasePackages = {"com.example.bikesales.orderorchestrator", "com.example.bikesales.infrastructure", "com.example.bikesales.checkout.domain"})
// However, this usually indicates a need to ensure Maven dependencies are correctly set up
// so that Spring Boot's auto-configuration or component scan can find beans from dependent JARs.
// For now, let's assume default component scan is sufficient for components within this service,
// and shared beans are pulled in via auto-configuration from dependent modules.

@SpringBootApplication
public class OrderOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderOrchestratorApplication.class, args);
    }

}
