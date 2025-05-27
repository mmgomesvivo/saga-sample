package com.example.bikesales.orderorchestrator.domain;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary result of an order placement attempt.")
public record OrderSummaryDto(
    @Schema(description = "Temporary tracking ID or final Order ID if available. Can be null if initiation failed very early.", nullable = true)
    String orderId,
    @Schema(description = "Status of the order placement request (e.g., CHECKOUT_INITIATED, VALIDATION_ERROR, ORDER_PLACEMENT_FAILED).", requiredMode = Schema.RequiredMode.REQUIRED)
    String status,
    @Schema(description = "Descriptive message about the outcome.", requiredMode = Schema.RequiredMode.REQUIRED)
    String message
) {
    public OrderSummaryDto {
        // orderId can be null as per instruction
        Objects.requireNonNull(status, "status cannot be null");
        Objects.requireNonNull(message, "message cannot be null");
    }
}
