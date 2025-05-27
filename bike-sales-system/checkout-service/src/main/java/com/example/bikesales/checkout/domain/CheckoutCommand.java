package com.example.bikesales.checkout.domain;

import java.util.List;
import java.util.Objects;

// CustomerId and BikeOrderItem are in the same package, so no explicit import needed for them.

public record CheckoutCommand(
    CustomerId customerId,
    List<BikeOrderItem> items
    // String shippingAddressId // Example of another field
) {
    public CheckoutCommand { // Compact constructor for validation
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be null or empty.");
        }
        // Ensure list is not modifiable from outside if desired, e.g., by using List.copyOf
        items = List.copyOf(items);
    }

    // Override equals and hashCode if List.copyOf makes a difference in how equality is perceived
    // or if there are other specific needs. For records, default implementations are usually fine.
    // However, since List.copyOf is used, ensure that the list content equality is what's desired.
    // Default record equals/hashCode should handle list content correctly.
}
