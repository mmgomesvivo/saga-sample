package com.example.bikesales.checkout.application.steps;

import com.example.bikesales.checkout.domain.CheckoutContext;
// BikeOrderItem is in com.example.bikesales.checkout.domain, no explicit import needed if this class is in the same package.
// However, this step class is in a subpackage 'application.steps', so explicit import might be needed if BikeOrderItem is not public
// or if there are package-private restrictions. Assuming BikeOrderItem is public.
// import com.example.bikesales.checkout.domain.BikeOrderItem; 
import com.example.bikesales.saga.SagaStep;
import com.example.bikesales.saga.SagaStepResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
// import java.util.stream.Collectors; // Not strictly needed for the simplified version

@Component
public class ReserveInventoryStep implements SagaStep<CheckoutContext> {

    @Override
    public CompletableFuture<SagaStepResult> execute(CheckoutContext context) {
        // Using the simpler logging from the instructions, but example logging is also fine.
        System.out.println("Executing ReserveInventoryStep for items: " + context.getItems());

        // Placeholder Logic: Simulate inventory check and reservation.
        // In a real system, this would interact with an Inventory Service.
        // For example, iterate through context.getItems() and call inventory service for each.

        // Simulate success for now as per instructions
        context.setInventoryReserved(true);
        System.out.println("ReserveInventoryStep: Inventory successfully reserved for all items.");
        return CompletableFuture.completedFuture(new SagaStepResult(true, "Inventory reserved successfully."));
        
        // Optional: Add a simulated failure path (not required by primary instructions but good for testing)
        // if (context.getItems().stream().anyMatch(item -> "FAIL_ITEM_ID".equals(item.bikeId().toString()))) {
        //     String failureMsg = "Inventory reservation failed: Simulated stockout for an item.";
        //     context.setInventoryReserved(false);
        //     context.setFailureReason(failureMsg);
        //     System.out.println("ReserveInventoryStep: " + failureMsg);
        //     return CompletableFuture.completedFuture(new SagaStepResult(false, failureMsg));
        // }
    }

    @Override
    public CompletableFuture<Void> compensate(CheckoutContext context) {
        System.out.println("ReserveInventoryStep: Compensating. Releasing inventory for items: " + context.getItems());

        // Placeholder: Simulate releasing inventory.
        // In a real system, this would call the Inventory Service to cancel reservations.
        // The example has a check for context.isInventoryReserved(), which is good practice.
        if (context.isInventoryReserved()) {
            context.setInventoryReserved(false); // Reflect that it's no longer reserved
            System.out.println("ReserveInventoryStep: Inventory reservations successfully cancelled.");
        } else {
            System.out.println("ReserveInventoryStep: No inventory was marked as reserved in context, no specific compensation action taken for inventory release.");
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getStepName() {
        return "ReserveInventoryStep";
    }
}
