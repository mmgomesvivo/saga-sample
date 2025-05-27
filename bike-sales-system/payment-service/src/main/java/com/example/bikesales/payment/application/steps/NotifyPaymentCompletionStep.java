package com.example.bikesales.payment.application.steps;

import com.example.bikesales.payment.domain.PaymentContext;
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class NotifyPaymentCompletionStep implements SagaStep<PaymentContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(PaymentContext context) {
        // Using the logging from the instruction
        System.out.println("Executing NotifyPaymentCompletionStep for Order ID: " + context.getOrderId().value() +
                           ", Order Status Updated: " + context.isOrderStatusUpdated());

        // Using the pre-condition check from the instruction
        if (!context.isOrderStatusUpdated()) { // Or check isPaymentConfirmed, depending on desired trigger
            String errorMsg = "Cannot send payment completion notification: Order status not updated or payment not confirmed.";
            System.out.println("NotifyPaymentCompletionStep: " + errorMsg);
            // Depending on criticality, this step might not fail the whole saga.
            // For placeholder consistency, can return failure, or success if notifications are best-effort.
            // Let's assume for now it should reflect that it couldn't perform its main action.
            context.setFailureReason(errorMsg);
            return CompletableFuture.completedFuture(new SagaStepResult(false, errorMsg));
        }

        // Using the simulation message from the instruction
        System.out.println("NotifyPaymentCompletionStep: Simulating sending payment completion notifications (e.g., to customer, shipping) for Order ID: " + context.getOrderId().value());
        
        // Using the result message from the instruction
        return CompletableFuture.completedFuture(
            new SagaStepResult(true, "Payment completion notifications sent for Order ID: " + context.getOrderId().value())
        );
    }

    @Override
    public CompletableFuture<Void> compensate(PaymentContext context) {
        // Using the logging from the instruction
        System.out.println("NotifyPaymentCompletionStep: Compensating for Order ID: " + context.getOrderId().value());

        // Using the placeholder logic from the instruction
        System.out.println("NotifyPaymentCompletionStep: Notifications are typically fire-and-forget. No direct compensation action taken in this placeholder.");

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "NotifyPaymentCompletionStep";
    }
}
