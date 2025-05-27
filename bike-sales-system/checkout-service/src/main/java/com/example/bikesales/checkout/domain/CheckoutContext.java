package com.example.bikesales.checkout.domain;

// CheckoutCommand, CustomerId, BikeOrderItem are in the same package.
import com.example.bikesales.domain.model.OrderId; // Import the shared OrderId
import java.util.List;

public class CheckoutContext {
    private final CheckoutCommand command;
    private OrderId orderId; // This will be set during the 'CreateOrderStep'
    private boolean isValidated = false;
    private boolean isInventoryReserved = false;
    private boolean isOrderCreated = false;
    private String failureReason;
    // Add more fields as needed, e.g., reservedItemDetails, paymentId, etc.

    private CheckoutContext(CheckoutCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("CheckoutCommand cannot be null when creating context.");
        }
        this.command = command;
    }

    public static CheckoutContext from(CheckoutCommand command) {
        return new CheckoutContext(command);
    }

    // Getters
    public CheckoutCommand getCommand() { return command; }
    public OrderId getOrderId() { return orderId; }
    public boolean isValidated() { return isValidated; }
    public boolean isInventoryReserved() { return isInventoryReserved; }
    public boolean isOrderCreated() { return isOrderCreated; }
    public String getFailureReason() { return failureReason; }

    // Setters (or methods with business meaning that change state)
    public void setOrderId(OrderId orderId) { this.orderId = orderId; }
    public void setValidated(boolean validated) { isValidated = validated; }
    public void setInventoryReserved(boolean inventoryReserved) { isInventoryReserved = inventoryReserved; }
    public void setOrderCreated(boolean orderCreated) { isOrderCreated = orderCreated; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    // Convenience method to access command details if needed
    public CustomerId getCustomerId() { return command.customerId(); }
    public List<BikeOrderItem> getItems() { return command.items(); }
}
