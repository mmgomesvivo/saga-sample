package com.example.bikesales.payment.application;

import com.example.bikesales.payment.application.steps.ConfirmPaymentStep;
import com.example.bikesales.payment.application.steps.NotifyPaymentCompletionStep;
import com.example.bikesales.payment.application.steps.ProcessPaymentStep;
import com.example.bikesales.payment.application.steps.UpdateOrderStatusStep;
import com.example.bikesales.payment.domain.PaymentContext;
import com.example.bikesales.payment.domain.PaymentRequestEvent;
import com.example.bikesales.saga.SagaDefinition;
import com.example.bikesales.saga.SagaOrchestrator;
import com.example.bikesales.saga.SagaStep;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.beans.factory.annotation.Value; // Not used if queue name hardcoded in annotation or using default
import org.springframework.stereotype.Service;

import java.util.List;
// CompletableFuture is used by executeSaga, but its result is handled by whenComplete, so no direct import needed for return type.

@Service
public class PaymentSagaOrchestrator {

    private final List<SagaStep<PaymentContext>> paymentSteps;
    private final SagaOrchestrator genericOrchestrator;

    public PaymentSagaOrchestrator(
            ProcessPaymentStep processPaymentStep,
            ConfirmPaymentStep confirmPaymentStep,
            UpdateOrderStatusStep updateOrderStatusStep,
            NotifyPaymentCompletionStep notifyPaymentCompletionStep, // Corrected parameter name from example's notifyPaymentStep
            SagaOrchestrator genericOrchestrator) {

        this.paymentSteps = List.of(
            processPaymentStep,
            confirmPaymentStep,
            updateOrderStatusStep,
            notifyPaymentCompletionStep // Corrected variable name from example
        );
        this.genericOrchestrator = genericOrchestrator;
    }

    @RabbitListener(queues = "${payment.processing.queue.name:payment.processing.queue}")
    public void handlePaymentRequest(PaymentRequestEvent event) {
        // Using logging from instruction (simpler)
        System.out.println("PaymentSagaOrchestrator: Received payment request for Order ID: " + event.orderId().value());

        PaymentContext context = PaymentContext.from(event);
        SagaDefinition<PaymentContext> sagaDefinition = new SagaDefinition<>("payment-saga", this.paymentSteps);

        // Optional: Log before execution as in example
        System.out.println("PaymentSagaOrchestrator: Starting SAGA execution for Order ID: " + event.orderId().value());

        genericOrchestrator.executeSaga(sagaDefinition, context)
            .whenComplete((sagaResult, throwable) -> {
                if (throwable != null) {
                    System.err.println("PaymentSagaOrchestrator: Saga execution for Order ID " + event.orderId().value() + " completed with an exception: " + throwable.getMessage());
                    // Potentially publish a PaymentFailedEvent with details from context.getFailureReason() or throwable
                } else {
                    if (sagaResult.success()) {
                        System.out.println("PaymentSagaOrchestrator: Payment SAGA for Order ID " + event.orderId().value() + " completed successfully.");
                        // Potentially publish a PaymentCompletedEvent
                    } else {
                        System.err.println("PaymentSagaOrchestrator: Payment SAGA for Order ID " + event.orderId().value() + " failed: " + sagaResult.message());
                        // Potentially publish a PaymentFailedEvent with details from sagaResult.message() or context.getFailureReason()
                    }
                }
            });
    }
}
