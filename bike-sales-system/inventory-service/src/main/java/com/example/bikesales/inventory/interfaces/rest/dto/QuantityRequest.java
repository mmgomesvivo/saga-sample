package com.example.bikesales.inventory.interfaces.rest.dto;

public record QuantityRequest(int quantity) {
    // No explicit validation here, will be handled in controller or service if needed,
    // or could add a compact constructor for validation if desired for all uses.
    // For example:
    // public QuantityRequest {
    //     if (quantity <= 0) {
    //         throw new IllegalArgumentException("Quantity must be positive.");
    //     }
    // }
}
