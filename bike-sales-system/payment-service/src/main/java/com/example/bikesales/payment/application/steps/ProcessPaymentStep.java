package com.example.bikesales.payment.application.steps;

import com.example.bikesales.payment.domain.PaymentContext;
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class ProcessPaymentStep implements SagaStep<PaymentContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(PaymentContext context) {
        // Using the more detailed logging from the example for amount
        System.out.println("Executing ProcessPaymentStep for Order ID: " + context.getOrderId().value() +
                           " Amount: " + context.getAmount().amount() + " " + context.getAmount().currency());
        System.out.println("Payment Details: " + context.getPaymentMethodDetails());

        // Simulate payment gateway interaction
        // Example: Check for a simulated failure condition
        // Assuming PaymentMethodDetails.details() is never null due to its own constructor.
        if ("FAIL_CARD".equals(context.getPaymentMethodDetails().details().get("cardNumber"))) {
            String errorMsg = "Payment processing failed: Card declined (simulated).";
            context.setPaymentProcessed(false);
            context.setFailureReason(errorMsg);
            System.out.println("ProcessPaymentStep: " + errorMsg);
            return CompletableFuture.completedFuture(new SagaStepResult(false, errorMsg));
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString();
        context.setPaymentTransactionId(transactionId);
        context.setPaymentProcessed(true);

        System.out.println("ProcessPaymentStep: Payment processed successfully. Transaction ID: " + transactionId);
        return CompletableFuture.completedFuture(
            new SagaStepResult(true, "Payment processed successfully. Transaction ID: " + transactionId)
        );
    }

    @Override
    public CompletableFuture<Void> compensate(PaymentContext context) {
        // Using the more detailed logging from the example for compensation
        System.out.println("ProcessPaymentStep: Compensating payment for Order ID: " + context.getOrderId().value() +
                           ". Transaction ID: " + context.getPaymentTransactionId());

        if (context.getPaymentTransactionId() != null && context.isPaymentProcessed()) {
            // Simulate refunding or voiding the payment
            System.out.println("ProcessPaymentStep: Simulating refund/void for Transaction ID: " + context.getPaymentTransactionId());
            context.setPaymentProcessed(false); // Update status
            // context.setPaymentTransactionId(null); // Optionally clear the transaction ID or mark as refunded
            System.out.println("ProcessPaymentStep: Refund/void processed successfully (simulated).");
        } else {
            // Using the more detailed logging from the example for this case
            System.out.println("ProcessPaymentStep: No transaction ID found or payment not marked as processed. No compensation action taken.");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "ProcessPaymentStep";
    }
}
