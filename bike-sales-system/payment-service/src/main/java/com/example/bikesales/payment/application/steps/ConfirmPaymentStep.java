package com.example.bikesales.payment.application.steps;

import com.example.bikesales.payment.domain.PaymentContext;
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ConfirmPaymentStep implements SagaStep<PaymentContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(PaymentContext context) {
        // Using the example's logging which includes Payment Processed status
        System.out.println("Executing ConfirmPaymentStep for Order ID: " + context.getOrderId().value() +
                           ", Transaction ID: " + context.getPaymentTransactionId() +
                           ", Payment Processed: " + context.isPaymentProcessed());

        if (!context.isPaymentProcessed() || context.getPaymentTransactionId() == null) {
            String errorMsg = "Payment confirmation cannot proceed: Payment not processed or transaction ID is missing.";
            System.out.println("ConfirmPaymentStep: " + errorMsg);
            // context.setPaymentConfirmed(false); // Example sets this, instruction doesn't explicitly. Good for explicitness.
            context.setFailureReason(errorMsg);
            return CompletableFuture.completedFuture(new SagaStepResult(false, errorMsg));
        }

        // Simulate internal confirmation logic
        context.setPaymentConfirmed(true);
        // Using the instruction's message for success logging
        System.out.println("ConfirmPaymentStep: Payment confirmed successfully for Transaction ID: " + context.getPaymentTransactionId());
        // Using the instruction's message for result
        return CompletableFuture.completedFuture(
            new SagaStepResult(true, "Payment confirmed for Transaction ID: " + context.getPaymentTransactionId())
        );
    }

    @Override
    public CompletableFuture<Void> compensate(PaymentContext context) {
        // Using the example's logging which includes Payment Confirmed status
        System.out.println("ConfirmPaymentStep: Compensating payment confirmation for Order ID: " + context.getOrderId().value() +
                           ", Transaction ID: " + context.getPaymentTransactionId() +
                           ", Payment Confirmed: " + context.isPaymentConfirmed());

        // Using the instruction's placeholder logic and logging for compensation
        System.out.println("ConfirmPaymentStep: Undoing internal payment confirmation (simulated). Actual compensation usually relies on ProcessPaymentStep's compensation (e.g., refund).");
        if (context.isPaymentConfirmed()) {
            context.setPaymentConfirmed(false); // Revert the confirmation status
            System.out.println("ConfirmPaymentStep: Internal payment confirmation status reverted.");
        } else {
            System.out.println("ConfirmPaymentStep: Payment was not marked as confirmed. No compensation action for confirmation status.");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "ConfirmPaymentStep";
    }
}
