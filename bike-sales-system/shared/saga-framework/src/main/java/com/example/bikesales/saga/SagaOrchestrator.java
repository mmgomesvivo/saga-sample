package com.example.bikesales.saga;

import org.springframework.stereotype.Component;
// Assuming RabbitTemplate is available, though not strictly required for this initial implementation's core logic
// import org.springframework.amqp.rabbit.core.RabbitTemplate; 

import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

@Component
public class SagaOrchestrator {

    // private final RabbitTemplate rabbitTemplate; // For potential future use
    private final SagaStateRepository sagaStateRepository;

    // Updated constructor to remove RabbitTemplate for now as it's not used
    // and will cause Spring context to fail if RabbitMQ is not configured.
    public SagaOrchestrator(/*RabbitTemplate rabbitTemplate,*/ SagaStateRepository sagaStateRepository) {
        // this.rabbitTemplate = rabbitTemplate;
        this.sagaStateRepository = sagaStateRepository;
    }

    public <T> CompletableFuture<SagaResult> executeSaga(SagaDefinition<T> sagaDefinition, T context) {
        CompletableFuture<SagaResult> overallResult = new CompletableFuture<>();
        Stack<SagaStep<T>> completedStepsForCompensation = new Stack<>();
        List<SagaStep<T>> steps = sagaDefinition.getSteps();

        // Chain futures sequentially
        CompletableFuture<Void> currentStepFuture = CompletableFuture.completedFuture(null);

        for (SagaStep<T> step : steps) {
            currentStepFuture = currentStepFuture.thenCompose(voidResult -> {
                // Placeholder for state update:
                // sagaStateRepository.saveState(sagaDefinition.getSagaName(), step.getStepName(), context, SagaStateRepository.SagaStatus.PENDING);
                System.out.println("Executing step: " + step.getStepName()); // Logging for clarity

                return step.execute(context)
                    .thenAccept(stepResult -> {
                        if (stepResult.success()) {
                            System.out.println("Step " + step.getStepName() + " succeeded."); // Logging
                            completedStepsForCompensation.push(step);
                            // Placeholder for state update:
                            // sagaStateRepository.updateStateStatus(sagaDefinition.getSagaName(), step.getStepName(), SagaStateRepository.SagaStatus.COMPLETED);
                        } else {
                            System.out.println("Step " + step.getStepName() + " failed: " + stepResult.message()); // Logging
                            // Placeholder for state update:
                            // sagaStateRepository.updateStateStatus(sagaDefinition.getSagaName(), step.getStepName(), SagaStateRepository.SagaStatus.FAILED);
                            throw new RuntimeException("Saga step " + step.getStepName() + " failed: " + stepResult.message());
                        }
                    });
            });
        }

        currentStepFuture.thenRun(() -> {
            System.out.println("Saga '" + sagaDefinition.getSagaName() + "' completed successfully."); // Logging
            overallResult.complete(new SagaResult(true, "Saga '" + sagaDefinition.getSagaName() + "' completed successfully."));
        }).exceptionally(ex -> {
            System.out.println("Saga '" + sagaDefinition.getSagaName() + "' failed. Starting compensation."); // Logging
            // Compensation logic
            CompletableFuture<Void> compensationFuture = CompletableFuture.completedFuture(null);
            while (!completedStepsForCompensation.isEmpty()) {
                SagaStep<T> stepToCompensate = completedStepsForCompensation.pop();
                System.out.println("Compensating step: " + stepToCompensate.getStepName()); // Logging
                // Placeholder for state update:
                // sagaStateRepository.updateStateStatus(sagaDefinition.getSagaName(), stepToCompensate.getStepName(), SagaStateRepository.SagaStatus.COMPENSATING);

                compensationFuture = compensationFuture.thenCompose(voidResult ->
                    stepToCompensate.compensate(context)
                        .thenRun(() -> {
                             System.out.println("Step " + stepToCompensate.getStepName() + " compensated."); // Logging
                            // Placeholder for state update:
                            // sagaStateRepository.updateStateStatus(sagaDefinition.getSagaName(), stepToCompensate.getStepName(), SagaStateRepository.SagaStatus.COMPENSATED);
                        })
                );
            }
            compensationFuture.whenComplete((compResult, compEx) -> {
                String failureMessage = "Saga '" + sagaDefinition.getSagaName() + "' failed";
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex; // Get the root cause if available
                
                if (cause != null) {
                    failureMessage += ": " + cause.getMessage();
                }

                if (compEx != null) {
                    System.out.println("Compensation failed: " + compEx.getMessage()); // Logging
                    failureMessage += ". Compensation also failed: " + compEx.getMessage();
                } else {
                    System.out.println("Compensation completed for saga: " + sagaDefinition.getSagaName()); // Logging
                }
                overallResult.complete(new SagaResult(false, failureMessage));
            });
            return null;
        });
        return overallResult;
    }
}
