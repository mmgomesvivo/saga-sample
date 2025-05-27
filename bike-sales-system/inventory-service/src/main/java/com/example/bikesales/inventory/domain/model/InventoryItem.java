package com.example.bikesales.inventory.domain.model;

import com.example.bikesales.domain.model.BikeId;
import java.util.Objects;

public class InventoryItem {
    private final BikeId bikeId;
    private int quantityInStock;
    private int quantityReserved;

    public InventoryItem(BikeId bikeId, int initialQuantityInStock) {
        this.bikeId = Objects.requireNonNull(bikeId, "BikeId cannot be null.");
        if (initialQuantityInStock < 0) {
            throw new IllegalArgumentException("Initial quantity in stock cannot be negative.");
        }
        this.quantityInStock = initialQuantityInStock;
        this.quantityReserved = 0;
    }

    // Getter Methods
    public BikeId getBikeId() {
        return bikeId;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public int getQuantityReserved() {
        return quantityReserved;
    }

    public int getAvailableToReserve() {
        return quantityInStock - quantityReserved;
    }

    // Business Methods
    public boolean canReserve(int quantityToReserve) {
        // Instruction: quantityToReserve > 0 and getAvailableToReserve() >= quantityToReserve
        // Example: quantityToReserve <= 0 return false; return getAvailableToReserve() >= quantityToReserve;
        // Combining these for robustness:
        if (quantityToReserve <= 0) {
            return false; // Cannot reserve non-positive or zero quantity
        }
        return getAvailableToReserve() >= quantityToReserve;
    }

    public void reserveStock(int quantityToReserve) {
        // Instruction: If !canReserve, throw. this.quantityReserved += quantityToReserve;
        // Example: If quantityToReserve <= 0, throw. If !canReserve, throw. this.quantityReserved += quantityToReserve;
        // The example's positive check is good, though canReserve already handles it.
        if (quantityToReserve <= 0) { // From example, good practice
            throw new IllegalArgumentException("Quantity to reserve must be positive.");
        }
        if (!canReserve(quantityToReserve)) {
            throw new IllegalStateException("Not enough stock available to reserve " + quantityToReserve +
                                            " for bike " + (bikeId != null ? bikeId.value() : "null") +
                                            ". Available to reserve: " + getAvailableToReserve());
        }
        this.quantityReserved += quantityToReserve;
    }

    public void releaseStockReservation(int quantityToRelease) {
        if (quantityToRelease <= 0) {
            throw new IllegalArgumentException("Quantity to release must be positive.");
        }
        // Instruction: this.quantityReserved -= quantityToRelease; If < 0, set to 0 and log warning.
        // Example: If this.quantityReserved < quantityToRelease, log warning and set to 0. Else, subtract.
        // The example's logic is more robust before subtraction.
        if (this.quantityReserved < quantityToRelease) {
            System.err.println("Warning: Attempting to release " + quantityToRelease +
                               " items for bike " + (bikeId != null ? bikeId.value() : "null") +
                               ", but only " + this.quantityReserved + " were reserved. Setting reserved to 0.");
            this.quantityReserved = 0;
        } else {
            this.quantityReserved -= quantityToRelease;
        }
    }

    public void confirmStockReservation(int quantityToConfirm) {
        if (quantityToConfirm <= 0) {
            throw new IllegalArgumentException("Quantity to confirm must be positive.");
        }
        if (quantityToConfirm > this.quantityReserved) {
            throw new IllegalStateException("Cannot confirm reservation of " + quantityToConfirm +
                                            " items; only " + this.quantityReserved +
                                            " are reserved for bike " + (bikeId != null ? bikeId.value() : "null"));
        }
        // Instruction: If quantityToConfirm > this.quantityInStock, throw (data inconsistency).
        // Example also has this check.
        if (quantityToConfirm > this.quantityInStock) {
             throw new IllegalStateException("Data inconsistency: Cannot confirm reservation of " + quantityToConfirm +
                                            " items; only " + this.quantityInStock +
                                            " are physically in stock for bike " + (bikeId != null ? bikeId.value() : "null") +
                                            ". Reserved quantity was " + this.quantityReserved);
        }
        this.quantityInStock -= quantityToConfirm;
        this.quantityReserved -= quantityToConfirm;
    }

    public void addToStock(int quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity to add to stock must be positive.");
        }
        this.quantityInStock += quantityToAdd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Objects.equals(bikeId, that.bikeId); // Based on bikeId as per instruction's consideration
    }

    @Override
    public int hashCode() {
        return Objects.hash(bikeId); // Based on bikeId
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
               "bikeId=" + (bikeId != null ? bikeId.value() : "null") +
               ", quantityInStock=" + quantityInStock +
               ", quantityReserved=" + quantityReserved +
               ", availableToReserve=" + getAvailableToReserve() +
               '}';
    }
}
