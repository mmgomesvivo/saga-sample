package com.example.bikesales.orderorchestrator.domain;

import com.example.bikesales.checkout.domain.BikeOrderItem; // From checkout-service
import com.example.bikesales.checkout.domain.BikeOrderItem; // From checkout-service
import com.example.bikesales.domain.model.CustomerId;   // From shared domain-events

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Command object for placing a new order.")
public record PlaceOrderCommand(
    @Schema(description = "Unique identifier of the customer placing the order.", requiredMode = Schema.RequiredMode.REQUIRED)
    CustomerId customerId,
    @Schema(description = "List of items to be included in the order. Must not be empty.", requiredMode = Schema.RequiredMode.REQUIRED)
    List<BikeOrderItem> items
) {
    public PlaceOrderCommand {
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(items, "items cannot be null");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be empty.");
        }
        // Ensure the list is unmodifiable
        items = List.copyOf(items);
    }
}
