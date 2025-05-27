package com.example.bikesales.payment.domain;

import com.example.bikesales.domain.model.Money;
import com.example.bikesales.domain.model.OrderId;
import java.util.Map; // For PaymentMethodDetails
import java.util.UUID; // For local CustomerId

// Local CustomerId for payment context
record CustomerId(UUID id) {
    public CustomerId { // Compact constructor for validation
        if (id == null) {
            throw new IllegalArgumentException("Customer ID (UUID) cannot be null.");
        }
    }
}

// Local PaymentMethodDetails for payment context
record PaymentMethodDetails(String type, Map<String, String> details) {
    public PaymentMethodDetails {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Payment method type cannot be null or blank.");
        }
        // Ensure details map is not null and is made unmodifiable
        details = (details == null) ? Map.of() : Map.copyOf(details);
    }
}

public record PaymentRequestEvent(
    OrderId orderId,
    Money amount,
    CustomerId customerId, // Uses the local CustomerId defined above
    PaymentMethodDetails paymentMethodDetails // Uses the local PaymentMethodDetails defined above
) {
    public PaymentRequestEvent { // Compact constructor for validation
        if (orderId == null) throw new IllegalArgumentException("Order ID cannot be null.");
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null.");
        if (customerId == null) throw new IllegalArgumentException("Customer ID cannot be null.");
        if (paymentMethodDetails == null) throw new IllegalArgumentException("Payment method details cannot be null.");
    }
}
