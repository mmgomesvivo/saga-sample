package com.example.bikesales.domain.model;

import java.util.UUID;

public record CustomerId(UUID id) {
    public CustomerId {
        if (id == null) {
            throw new IllegalArgumentException("Customer UUID cannot be null.");
        }
    }
}
