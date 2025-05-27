package com.example.bikesales.saga;

import java.util.List;

public class SagaDefinition<T> {
    private final String sagaName;
    private final List<SagaStep<T>> steps;

    public SagaDefinition(String sagaName, List<SagaStep<T>> steps) {
        if (sagaName == null || sagaName.isBlank()) {
            throw new IllegalArgumentException("Saga name cannot be null or blank.");
        }
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Saga steps cannot be null or empty.");
        }
        this.sagaName = sagaName;
        this.steps = List.copyOf(steps); // Ensure immutability
    }

    public String getSagaName() {
        return sagaName;
    }

    public List<SagaStep<T>> getSteps() {
        return steps;
    }
}
