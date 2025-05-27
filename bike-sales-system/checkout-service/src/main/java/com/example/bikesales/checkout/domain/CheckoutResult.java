package com.example.bikesales.checkout.domain;

// No specific imports needed unless OrderId type is fully qualified and not from same package.
// Assuming OrderId might be null if saga fails before OrderId generation.
// For simplicity, using String for orderId here, can be changed to com.example.bikesales.domain.model.OrderId if preferred and handled.

public record CheckoutResult(
    boolean success,
    String message,
    String orderId // String representation of OrderId, or null
) {}
