package com.example.bikesales.domain.events;

import com.example.bikesales.domain.model.CustomerId;
import com.example.bikesales.domain.model.Money;
import com.example.bikesales.domain.model.Order;
import com.example.bikesales.domain.model.OrderId;
import com.example.bikesales.domain.model.OrderItemData;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record OrderCreatedEvent(
    OrderId orderId,
    CustomerId customerId,
    List<OrderItemData> items, // Order.items() already returns an unmodifiable list
    Money totalAmount,
    Instant createdAt
) implements DomainEvent {

    // Canonical constructor for validation (optional but good practice)
    public OrderCreatedEvent {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Objects.requireNonNull(customerId, "customerId cannot be null");
        Objects.requireNonNull(items, "items cannot be null");
        // It's good practice to ensure the list is unmodifiable if it weren't already.
        // However, Order.items() already returns an unmodifiable list (due to List.copyOf in Order's constructor).
        // So, an explicit List.copyOf(items) here would be redundant but harmless.
        // For this example, we'll rely on the source (Order.items()) providing an unmodifiable list.
        Objects.requireNonNull(totalAmount, "totalAmount cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    public static OrderCreatedEvent from(Order order) {
        Objects.requireNonNull(order, "Order cannot be null for creating OrderCreatedEvent.");
        // The Order class's items() method returns an unmodifiable list due to List.copyOf in its constructor.
        return new OrderCreatedEvent(
            order.id(),
            order.customerId(),
            order.items(),
            order.totalAmount(),
            order.createdAt()
        );
    }
}
