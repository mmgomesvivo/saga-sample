package com.example.bikesales.inventory.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for operations requiring a quantity.")
public record QuantityRequest(
    @Schema(description = "The quantity for the operation (e.g., reserve, release, restock). Must be a positive integer.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    int quantity
) {
    // No explicit validation here, will be handled in controller or service if needed,
    // or could add a compact constructor for validation if desired for all uses.
    // For example:
    // public QuantityRequest {
    //     if (quantity <= 0) {
    //         throw new IllegalArgumentException("Quantity must be positive.");
    //     }
    // }
}
