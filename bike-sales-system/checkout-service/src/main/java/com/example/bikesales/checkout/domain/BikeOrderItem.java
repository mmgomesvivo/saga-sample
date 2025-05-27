package com.example.bikesales.checkout.domain;

// Assuming BikeId will be a String for now as per instruction,
// though in a real system it might be a more complex type like com.example.bikesales.domain.model.BikeId
import com.example.bikesales.domain.model.BikeId; // Preferring the shared domain model

public record BikeOrderItem(BikeId bikeId, int quantity) {
    public BikeOrderItem {
        if (bikeId == null) {
            throw new IllegalArgumentException("Bike ID cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
    }
}
