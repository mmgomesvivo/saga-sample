package com.example.bikesales.domain.model;

import java.util.Objects;

public final class BikeSpecifications {
    private final boolean inStock;

    public BikeSpecifications(boolean inStock) {
        this.inStock = inStock;
    }

    public boolean isInStock() {
        return inStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BikeSpecifications that = (BikeSpecifications) o;
        return inStock == that.inStock;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inStock);
    }

    @Override
    public String toString() {
        return "BikeSpecifications{" +
               "inStock=" + inStock +
               '}';
    }
}
