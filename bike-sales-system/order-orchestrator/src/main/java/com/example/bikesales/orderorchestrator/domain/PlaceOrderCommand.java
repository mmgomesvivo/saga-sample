package com.example.bikesales.orderorchestrator.domain;

import com.example.bikesales.checkout.domain.BikeOrderItem; // From checkout-service
import com.example.bikesales.domain.model.CustomerId;   // From shared domain-events

import java.util.List;
import java.util.Objects;

public record PlaceOrderCommand(
    CustomerId customerId,
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
