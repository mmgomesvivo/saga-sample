package com.example.bikesales.checkout.domain;

import java.util.UUID;

public record CustomerId(UUID id) {
    public CustomerId {
        if (id == null) {
            throw new IllegalArgumentException("Customer ID (UUID) cannot be null.");
        }
    }
}
