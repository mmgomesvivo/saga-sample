package com.example.bikesales.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

// OrderId, CustomerId, OrderItemData, Money are in this package.

public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderItemData> items;
    private final Money totalAmount;
    private final Instant createdAt;
    // private OrderStatus status; // Placeholder for a potential OrderStatus enum/class

    public Order(OrderId id, CustomerId customerId, List<OrderItemData> items, Money totalAmount, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        
        // Defensive copy and validation for items
        Objects.requireNonNull(items, "Items list cannot be null");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }
        this.items = List.copyOf(items); 
        
        this.totalAmount = Objects.requireNonNull(totalAmount, "Total amount cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation timestamp cannot be null");
        // this.status = OrderStatus.PENDING; // Example default status
    }

    // Accessor methods
    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public List<OrderItemData> items() {
        // Returns an unmodifiable list because List.copyOf was used in the constructor
        return items;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Instant createdAt() {
        return createdAt;
    }

    // public OrderStatus getStatus() { return status; }
    // public void setStatus(OrderStatus status) { this.status = status; } // If mutable status is needed

    // Standard overrides for equals, hashCode, and toString are good practice for entity-like classes.
    // For brevity in this placeholder, they are omitted but should be considered.
    // Example:
    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) return true;
    //     if (o == null || getClass() != o.getClass()) return false;
    //     Order order = (Order) o;
    //     return Objects.equals(id, order.id); // Typically, ID equality defines entity equality
    // }
    //
    // @Override
    // public int hashCode() {
    //     return Objects.hash(id); // Typically, ID hashcode defines entity hashcode
    // }
    //
    // @Override
    // public String toString() {
    //     return "Order{" +
    //            "id=" + id +
    //            ", customerId=" + customerId +
    //            ", items=" + items +
    //            ", totalAmount=" + totalAmount +
    //            ", createdAt=" + createdAt +
    //            // ", status=" + status +
    //            '}';
    // }
}
