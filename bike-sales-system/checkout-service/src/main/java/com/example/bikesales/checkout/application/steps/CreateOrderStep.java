package com.example.bikesales.checkout.application.steps;

import com.example.bikesales.checkout.domain.CheckoutContext;
import com.example.bikesales.domain.model.OrderId; // Ensure this is imported
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CreateOrderStep implements SagaStep<CheckoutContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(CheckoutContext context) {
        System.out.println("Executing CreateOrderStep for customer: " + context.getCustomerId() +
                           ", items: " + context.getItems());
                           // Example also logs: ", inventory reserved: " + context.isInventoryReserved());

        // Check if prerequisite steps were successful, e.g., inventory reservation
        if (!context.isInventoryReserved()) {
            String failureMsg = "Order creation cannot proceed as inventory was not reserved.";
            // context.setOrderCreated(false); // Not strictly necessary as it's default false
            context.setFailureReason(failureMsg);
            System.out.println("CreateOrderStep: " + failureMsg);
            return CompletableFuture.completedFuture(new SagaStepResult(false, failureMsg));
        }
        
        if (!context.isValidated()) { // Another example pre-condition check
            String failureMsg = "Order creation cannot proceed as order was not validated.";
            context.setFailureReason(failureMsg);
            System.out.println("CreateOrderStep: " + failureMsg);
            return CompletableFuture.completedFuture(new SagaStepResult(false, failureMsg));
        }


        // Placeholder Logic for order creation:
        OrderId newOrderId = OrderId.generate();
        context.setOrderId(newOrderId);
        context.setOrderCreated(true);

        System.out.println("CreateOrderStep: Order successfully created with ID: " + newOrderId.value());
        // In a real system:
        // 1. Create Order entity from context.getCommand() and newOrderId.
        // 2. Persist Order entity.
        // 3. Optionally, publish OrderCreatedEvent (though event publishing might be its own step or an async task).

        return CompletableFuture.completedFuture(
            new SagaStepResult(true, "Order created successfully with ID: " + newOrderId.value())
        );
    }

    @Override
    public CompletableFuture<Void> compensate(CheckoutContext context) {
        System.out.println("CreateOrderStep: Compensating. Attempting to cancel order with ID: " +
                           (context.getOrderId() != null ? context.getOrderId().value() : "N/A"));
                           // Example also logs: ", order created status: " + context.isOrderCreated());

        // Placeholder Logic for order cancellation:
        if (context.getOrderId() != null && context.isOrderCreated()) {
            // In a real system:
            // 1. Update order status to CANCELLED in the database.
            // 2. Optionally, publish OrderCancelledEvent.
            context.setOrderCreated(false); // Reflect that it's no longer considered created
            System.out.println("CreateOrderStep: Order " + context.getOrderId().value() + " successfully cancelled (simulated).");
        } else {
            System.out.println("CreateOrderStep: No order ID found in context or order not marked as created, no compensation action taken for order cancellation.");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "CreateOrderStep";
    }
}
