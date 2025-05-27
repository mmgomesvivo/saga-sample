package com.example.bikesales.orderorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition; // Added import
import io.swagger.v3.oas.annotations.info.Info;         // Added import

// If shared configurations (like RabbitMQConfiguration from common-infrastructure)
// or components from other base packages (like checkout.domain for BikeOrderItem)
// are not automatically picked up due to module structure and dependencies,
// scanBasePackages might be needed.
// e.g., @SpringBootApplication(scanBasePackages = {"com.example.bikesales.orderorchestrator", "com.example.bikesales.infrastructure", "com.example.bikesales.checkout.domain"})
// However, this usually indicates a need to ensure Maven dependencies are correctly set up
// so that Spring Boot's auto-configuration or component scan can find beans from dependent JARs.
// For now, let's assume default component scan is sufficient for components within this service,
// and shared beans are pulled in via auto-configuration from dependent modules.

@OpenAPIDefinition(
    info = @Info(
        title = "Order Orchestrator API",
        version = "v1.0.0", // Using a more specific version than just "v1"
        description = "API for placing and managing customer orders for the Bike Sales System."
    )
)
@SpringBootApplication
public class OrderOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderOrchestratorApplication.class, args);
    }

}
