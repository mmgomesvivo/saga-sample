package com.example.bikesales.checkout.application.steps;

import com.example.bikesales.checkout.domain.CheckoutContext;
// OrderId is in com.example.bikesales.domain.model, needs import if context.getOrderId().value() is used.
// CustomerId is in com.example.bikesales.checkout.domain, no explicit import needed if public.
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class SendNotificationStep implements SagaStep<CheckoutContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(CheckoutContext context) {
        // Using the logging from the instructions
        System.out.println("Executing SendNotificationStep for order ID: " +
                           (context.getOrderId() != null ? context.getOrderId().value() : "N/A") +
                           " to customer: " + context.getCustomerId());

        // Placeholder Logic:
        // Check if context.isOrderCreated() is true and context.getOrderId() is not null.
        // This check is somewhat redundant if CreateOrderStep is a prerequisite and the orchestrator
        // correctly halts on failure. However, it's good for step self-containment.
        if (!context.isOrderCreated() || context.getOrderId() == null) {
            String errorMsg = "SendNotificationStep: Cannot send notification because order was not marked as created or OrderId is null in context.";
            System.out.println(errorMsg);
            // Depending on criticality, this step might not fail the whole saga.
            // For this placeholder, let's return failure to indicate an unexpected state.
            // In many systems, notification failures are logged but don't trigger saga rollback.
            return CompletableFuture.completedFuture(new SagaStepResult(false, errorMsg));
        }

        System.out.println("SendNotificationStep: Simulating sending order confirmation notification for order " +
                           context.getOrderId().value() + " to customer " + context.getCustomerId());

        return CompletableFuture.completedFuture(
            new SagaStepResult(true, "Notification sent successfully for order: " + context.getOrderId().value())
        );
    }

    @Override
    public CompletableFuture<Void> compensate(CheckoutContext context) {
        // Using the logging from the instructions
        System.out.println("SendNotificationStep: Compensating. Order ID: " +
                           (context.getOrderId() != null ? context.getOrderId().value() : "N/A"));

        // Placeholder Logic from instructions:
        System.out.println("SendNotificationStep: Notifications are typically fire-and-forget or require complex compensation (e.g., 'disregard previous message'). No direct compensation action taken in this placeholder.");

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "SendNotificationStep";
    }
}
