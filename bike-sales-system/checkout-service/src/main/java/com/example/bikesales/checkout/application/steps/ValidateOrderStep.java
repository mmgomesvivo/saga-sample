package com.example.bikesales.checkout.application.steps;

import com.example.bikesales.checkout.domain.CheckoutContext;
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ValidateOrderStep implements SagaStep<CheckoutContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(CheckoutContext context) {
        System.out.println("Executing ValidateOrderStep for customer: " + context.getCustomerId());

        // Example validation: Ensure items are present in the context.
        // CheckoutCommand already validates this, but steps can have their own checks or more detailed ones.
        if (context.getItems() == null || context.getItems().isEmpty()) {
            String failureMsg = "Order validation failed: No items in context.";
            context.setValidated(false);
            context.setFailureReason(failureMsg);
            System.out.println("ValidateOrderStep: " + failureMsg);
            return CompletableFuture.completedFuture(new SagaStepResult(false, failureMsg));
        }

        // Add more validation logic here (e.g., check customer status, item availability stubs, etc.)
        // For this placeholder, assume simple validation passes if items are present.

        context.setValidated(true);
        System.out.println("ValidateOrderStep: Order details appear valid.");
        return CompletableFuture.completedFuture(new SagaStepResult(true, "Order validated successfully."));
    }

    @Override
    public CompletableFuture<Void> compensate(CheckoutContext context) {
        System.out.println("ValidateOrderStep: Compensation called. No specific action taken as validation is typically non-transacting or idempotent. Context validated: " + context.isValidated());
        // If there were any temporary reservations or locks made during validation (unlikely for this step), undo here.
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "ValidateOrderStep";
    }
}
