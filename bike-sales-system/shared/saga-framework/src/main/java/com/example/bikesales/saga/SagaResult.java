package com.example.bikesales.saga;

public record SagaResult(boolean success, String message) {
    // Implicit constructor: public SagaResult(boolean success, String message) {}
}
