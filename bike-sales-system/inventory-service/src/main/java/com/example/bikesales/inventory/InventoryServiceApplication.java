package com.example.bikesales.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition; // Added import
import io.swagger.v3.oas.annotations.info.Info;         // Added import
// Optional: If component scan needs to be wider, use @SpringBootApplication(scanBasePackages = {"com.example.bikesales"})
// but for now, default scan from this package should be fine as shared components are in com.example.bikesales.infrastructure / .saga etc.
// and inventory-specific components are in sub-packages of com.example.bikesales.inventory.

@OpenAPIDefinition(
    info = @Info(
        title = "Inventory Service API",
        version = "v1.0.0",
        description = "API for managing bike inventory, stock levels, and reservations."
    )
)
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

}
