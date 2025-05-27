package com.example.bikesales.orderorchestrator.domain;

import java.util.Objects;

public record OrderSummaryDto(
    String orderId, // Could be a preliminary ID or actual OrderId string
    String status,
    String message
) {
    public OrderSummaryDto {
        // orderId can be null as per instruction
        Objects.requireNonNull(status, "status cannot be null");
        Objects.requireNonNull(message, "message cannot be null");
    }
}
