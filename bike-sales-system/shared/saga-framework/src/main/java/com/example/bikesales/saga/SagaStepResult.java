package com.example.bikesales.saga;

public record SagaStepResult(boolean success, String message) {
    // No explicit constructor or getters needed for a record
    // They are implicitly defined.
}
