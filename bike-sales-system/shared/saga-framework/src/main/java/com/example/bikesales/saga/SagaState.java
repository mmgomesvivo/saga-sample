package com.example.bikesales.saga;

// Import SagaStatus from SagaStateRepository if it's not implicitly available
// due to package structure and access modifiers.
// For a record in the same package, direct usage is fine.

public record SagaState<T>(
    String sagaId,
    String sagaType,
    SagaStateRepository.SagaStatus currentOverallStatus, // Assuming SagaStatus is accessible
    T sagaContext
) {
    // No explicit constructor, getters, equals, hashCode, or toString needed.
    // They are automatically provided for records.
}
