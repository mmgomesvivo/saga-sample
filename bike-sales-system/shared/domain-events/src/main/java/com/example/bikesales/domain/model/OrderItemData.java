package com.example.bikesales.domain.model;

// BikeId and Money are in the same package 'com.example.bikesales.domain.model'.
// Explicit imports are not strictly necessary for types in the same package.
// However, if they were in sub-packages or different related packages, they would be:
// import com.example.bikesales.domain.model.BikeId;
// import com.example.bikesales.domain.model.Money;

public record OrderItemData(
    BikeId bikeId,
    int quantity,
    Money unitPrice,
    Money itemTotal
) {
    public OrderItemData { // Compact constructor for validation
        if (bikeId == null) {
            throw new IllegalArgumentException("BikeId cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null.");
        }
        if (itemTotal == null) {
            throw new IllegalArgumentException("Item total cannot be null.");
        }
        // Optional: Validate that itemTotal is consistent with unitPrice and quantity.
        // This would require access to a Money.multiplyBy(BigDecimal) or similar logic,
        // and potentially a Quantity value object.
        // For example (if Quantity was available and Money had multiplyBy(Quantity)):
        // if (!itemTotal.equals(unitPrice.multiplyBy(new Quantity(quantity)))) {
        //     throw new IllegalArgumentException("Item total must equal unit price multiplied by quantity.");
        // }
    }
}
