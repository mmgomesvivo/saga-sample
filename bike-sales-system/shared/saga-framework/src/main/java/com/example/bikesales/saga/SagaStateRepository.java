package com.example.bikesales.saga;

// No specific imports needed for this interface structure yet,
// but they would be added if specific types beyond T were used in method signatures.

public interface SagaStateRepository {

    enum SagaStatus {
        PENDING,    // Saga initialized, first step about to be processed
        PROCESSING, // Saga is in progress, one or more steps are being executed
        COMPLETED,  // All steps in the saga completed successfully
        COMPENSATING, // One or more steps failed, compensation is in progress
        COMPENSATED, // All necessary compensations have been successfully performed
        FAILED      // Saga failed and compensation also failed or was not possible
    }

    /**
     * Saves the initial state of a new saga instance.
     * @param sagaId The unique ID of the saga.
     * @param sagaType A string identifying the type or name of the saga definition.
     * @param context The initial context data for the saga.
     * @param <T> The type of the saga context.
     */
    <T> void saveInitialState(String sagaId, String sagaType, T context, SagaStatus initialStatus);

    /**
     * Updates the state of a specific step within a saga.
     * This can also be used to update the overall saga status by using a common/summary stepName.
     * @param sagaId The unique ID of the saga.
     * @param stepName The name of the step being updated.
     * @param status The new status of this step.
     * @param currentContext The current context of the saga (can be null if only status is updated).
     * @param <T> The type of the saga context.
     */
    <T> void updateStepState(String sagaId, String stepName, SagaStatus status, T currentContext);

    /**
     * Finds and retrieves the overall state of a saga instance.
     * This would typically fetch the latest known state or a summary.
     * @param sagaId The unique ID of the saga.
     * @param <T> The type of the saga context.
     * @return The current state of the saga, or null/empty if not found.
     */
    <T> SagaState<T> findSagaState(String sagaId);

    // In a real implementation, you might also have methods like:
    // <T> void saveStepCompensation(String sagaId, String stepName, SagaStatus compensationStatus);
    // List<SagaStepAttempt> findStepAttempts(String sagaId); // To see history
}
