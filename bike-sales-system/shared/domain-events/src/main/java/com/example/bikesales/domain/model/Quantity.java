package com.example.bikesales.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Quantity {
    private final BigDecimal value;

    public Quantity(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity value cannot be null or negative.");
        }
        this.value = value;
    }

    public Quantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity value cannot be negative.");
        }
        this.value = BigDecimal.valueOf(value);
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return Objects.equals(value, quantity.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Quantity{" +
               "value=" + value +
               '}';
    }
}
