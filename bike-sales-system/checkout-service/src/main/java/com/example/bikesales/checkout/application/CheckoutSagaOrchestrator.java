package com.example.bikesales.checkout.application;

import com.example.bikesales.checkout.application.steps.CreateOrderStep;
import com.example.bikesales.checkout.application.steps.ReserveInventoryStep;
import com.example.bikesales.checkout.application.steps.SendNotificationStep;
import com.example.bikesales.checkout.application.steps.ValidateOrderStep;
import com.example.bikesales.checkout.domain.CheckoutCommand;
import com.example.bikesales.checkout.domain.CheckoutContext;
import com.example.bikesales.checkout.domain.CheckoutResult;
import com.example.bikesales.saga.SagaDefinition;
import com.example.bikesales.saga.SagaOrchestrator;
import com.example.bikesales.saga.SagaStep;
// com.example.bikesales.domain.model.OrderId is imported by CheckoutContext,
// and its .value().toString() is used, so no direct import needed here unless for explicit type casting.

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CheckoutSagaOrchestrator {

    private final List<SagaStep<CheckoutContext>> sagaSteps;
    private final SagaOrchestrator genericOrchestrator;

    public CheckoutSagaOrchestrator(
            ValidateOrderStep validateOrderStep,
            ReserveInventoryStep reserveInventoryStep,
            CreateOrderStep createOrderStep,
            SendNotificationStep sendNotificationStep,
            SagaOrchestrator genericOrchestrator) {

        this.sagaSteps = List.of(
            validateOrderStep,
            reserveInventoryStep,
            createOrderStep,
            sendNotificationStep
        );
        this.genericOrchestrator = genericOrchestrator;
    }

    public CompletableFuture<CheckoutResult> processCheckout(CheckoutCommand command) {
        CheckoutContext context = CheckoutContext.from(command);
        // Using "CheckoutSaga" as the saga name, can be more dynamic if needed
        SagaDefinition<CheckoutContext> sagaDefinition = new SagaDefinition<>("CheckoutSaga", this.sagaSteps);

        return genericOrchestrator.executeSaga(sagaDefinition, context)
            .thenApply(sagaResult -> {
                String orderIdStr = null;
                // Check if order ID exists in context; it might if CreateOrderStep was reached
                if (context.getOrderId() != null) {
                    orderIdStr = context.getOrderId().value().toString();
                    if (!sagaResult.success()) {
                        // Append a marker if saga failed but order ID was generated
                        orderIdStr += " (SagaFailed)";
                    }
                }
                // If saga failed and no orderId was ever set, orderIdStr remains null.
                // If saga succeeded, orderIdStr will be the generated ID (or null if CreateOrderStep somehow didn't set it, which would be an issue).
                return new CheckoutResult(sagaResult.success(), sagaResult.message(), orderIdStr);
            });
    }
}
