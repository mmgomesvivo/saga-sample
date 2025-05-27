package com.example.bikesales.payment.application.steps;

import com.example.bikesales.payment.domain.PaymentContext;
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class UpdateOrderStatusStep implements SagaStep<PaymentContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(PaymentContext context) {
        // Using the combined logging from instruction and example
        System.out.println("Executing UpdateOrderStatusStep for Order ID: " + context.getOrderId().value() +
                           ", Payment Confirmed: " + context.isPaymentConfirmed());

        if (!context.isPaymentConfirmed()) {
            // Using error message from instruction, slightly different from example's
            String errorMsg = "Update order status cannot proceed: Payment not confirmed.";
            System.out.println("UpdateOrderStatusStep: " + errorMsg);
            // Example sets context.setOrderStatusUpdated(false); here, which is good for explicitness.
            context.setOrderStatusUpdated(false); 
            context.setFailureReason(errorMsg); // Set failure reason as per instruction
            return CompletableFuture.completedFuture(new SagaStepResult(false, errorMsg));
        }

        // Using simulation message from instruction
        System.out.println("UpdateOrderStatusStep: Simulating sending command/event to Order Service to update status to PAID for Order ID: " + context.getOrderId().value());
        context.setOrderStatusUpdated(true);

        // Using result message from instruction
        return CompletableFuture.completedFuture(
            new SagaStepResult(true, "Order status update to PAID requested for Order ID: " + context.getOrderId().value())
        );
    }

    @Override
    public CompletableFuture<Void> compensate(PaymentContext context) {
        // Using the combined logging from instruction and example
        System.out.println("UpdateOrderStatusStep: Compensating order status update for Order ID: " + context.getOrderId().value() +
                           ", OrderStatusUpdated: " + context.isOrderStatusUpdated());

        if (context.isOrderStatusUpdated()) {
            // Using simulation message from instruction
            System.out.println("UpdateOrderStatusStep: Simulating sending command/event to Order Service to revert status (e.g., to PAYMENT_FAILED) for Order ID: " + context.getOrderId().value());
            context.setOrderStatusUpdated(false); // Revert status in context
            // Using specific log message from instruction
            System.out.println("UpdateOrderStatusStep: Order status update reverted (simulated).");
        } else {
            // Using specific log message from instruction
            System.out.println("UpdateOrderStatusStep: Order status was not marked as updated. No compensation action taken for status update.");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "UpdateOrderStatusStep";
    }
}
