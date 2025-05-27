package com.example.bikesales.checkout.application;

import com.example.bikesales.checkout.application.steps.CreateOrderStep;
import com.example.bikesales.checkout.application.steps.ReserveInventoryStep;
import com.example.bikesales.checkout.application.steps.SendNotificationStep;
import com.example.bikesales.checkout.application.steps.ValidateOrderStep;
import com.example.bikesales.checkout.application.steps.CreateOrderStep;
import com.example.bikesales.checkout.application.steps.ReserveInventoryStep;
import com.example.bikesales.checkout.application.steps.SendNotificationStep;
import com.example.bikesales.checkout.application.steps.ValidateOrderStep;
import com.example.bikesales.checkout.domain.CheckoutCommand;
import com.example.bikesales.checkout.domain.CheckoutContext;
// CheckoutResult is no longer returned by the primary method
import com.example.bikesales.saga.SagaDefinition;
import com.example.bikesales.saga.SagaOrchestrator;
import com.example.bikesales.saga.SagaStep;

import org.springframework.amqp.rabbit.annotation.RabbitListener; // Added
import org.springframework.stereotype.Service;
import java.util.List;
// import java.util.concurrent.CompletableFuture; // No longer returned

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

    // Old method removed or made private if needed for other purposes (e.g. testing)
    // public CompletableFuture<CheckoutResult> processCheckout(CheckoutCommand command) { ... }

    @RabbitListener(queues = "${checkout.processing.queue.name:checkout.processing.queue}")
    public void handleCheckoutCommand(CheckoutCommand command) {
        if (command == null) {
            System.err.println("CheckoutSagaOrchestrator: Received null CheckoutCommand. Ignoring.");
            return;
        }
        System.out.println("CheckoutSagaOrchestrator: Received CheckoutCommand for Customer: " + command.customerId().id());

        CheckoutContext context = CheckoutContext.from(command);
        SagaDefinition<CheckoutContext> sagaDefinition = new SagaDefinition<>("CheckoutSaga", this.sagaSteps);
        
        System.out.println("CheckoutSagaOrchestrator: Starting SAGA execution for Customer: " + command.customerId().id());

        genericOrchestrator.executeSaga(sagaDefinition, context)
            .whenComplete((sagaResult, throwable) -> {
                String orderIdInfo = (context.getOrderId() != null) ? context.getOrderId().value().toString() : "N/A";
                if (throwable != null) {
                    System.err.println("CheckoutSagaOrchestrator: Saga for Customer " + command.customerId().id() +
                                       ", OrderID attempted: " + orderIdInfo +
                                       " completed with an exception: " + throwable.getMessage());
                    // Log context.getFailureReason() if it's more specific
                    if (context.getFailureReason() != null) {
                         System.err.println("CheckoutSagaOrchestrator: Context failure reason: " + context.getFailureReason());
                    }
                } else {
                    if (sagaResult.success()) {
                        System.out.println("CheckoutSagaOrchestrator: Checkout SAGA for Customer " + command.customerId().id() +
                                           " completed successfully. Order ID: " + orderIdInfo);
                        // TODO: Publish OrderSuccessfullyProcessed event or similar
                    } else {
                        System.err.println("CheckoutSagaOrchestrator: Checkout SAGA for Customer " + command.customerId().id() +
                                           ", OrderID attempted: " + orderIdInfo +
                                           " failed: " + sagaResult.message() +
                                           (context.getFailureReason() != null ? ". Context Failure: " + context.getFailureReason() : ""));
                        // TODO: Publish OrderProcessingFailed event or similar
                    }
                }
            });
    }
}
