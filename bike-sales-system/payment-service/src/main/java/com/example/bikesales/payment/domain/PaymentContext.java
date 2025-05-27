package com.example.bikesales.payment.domain;

// PaymentRequestEvent, CustomerId, PaymentMethodDetails are in the same package.
import com.example.bikesales.domain.model.Money; // For convenience getter
import com.example.bikesales.domain.model.OrderId; // For convenience getter

public class PaymentContext {
    private final PaymentRequestEvent requestEvent;
    private String paymentTransactionId;
    private boolean isPaymentProcessed = false;
    private boolean isPaymentConfirmed = false; // Typically means payment gateway confirmed settlement
    private boolean isOrderStatusUpdated = false; // If this saga is responsible for updating order status via event
    private String failureReason;

    private PaymentContext(PaymentRequestEvent requestEvent) {
        this.requestEvent = requestEvent;
    }

    public static PaymentContext from(PaymentRequestEvent requestEvent) {
        if (requestEvent == null) {
            throw new IllegalArgumentException("PaymentRequestEvent cannot be null for PaymentContext.");
        }
        return new PaymentContext(requestEvent);
    }

    // Getters
    public PaymentRequestEvent getRequestEvent() { return requestEvent; }
    public String getPaymentTransactionId() { return paymentTransactionId; }
    public boolean isPaymentProcessed() { return isPaymentProcessed; }
    public boolean isPaymentConfirmed() { return isPaymentConfirmed; }
    public boolean isOrderStatusUpdated() { return isOrderStatusUpdated; }
    public String getFailureReason() { return failureReason; }

    // Setters
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    public void setPaymentProcessed(boolean paymentProcessed) { isPaymentProcessed = paymentProcessed; }
    public void setPaymentConfirmed(boolean paymentConfirmed) { isPaymentConfirmed = paymentConfirmed; }
    public void setOrderStatusUpdated(boolean orderStatusUpdated) { isOrderStatusUpdated = orderStatusUpdated; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    // Convenience getters from the event
    public OrderId getOrderId() { return requestEvent.orderId(); }
    public Money getAmount() { return requestEvent.amount(); }
    public CustomerId getCustomerId() { return requestEvent.customerId(); } // Uses local CustomerId
    public PaymentMethodDetails getPaymentMethodDetails() { return requestEvent.paymentMethodDetails(); }
}
