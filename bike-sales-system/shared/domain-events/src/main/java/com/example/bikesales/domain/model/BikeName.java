package com.example.bikesales.domain.model;

import java.util.Objects;

public final class BikeName {
    private final String value;

    public BikeName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BikeName value cannot be null or blank.");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BikeName bikeName = (BikeName) o;
        return Objects.equals(value, bikeName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BikeName{" +
               "value='" + value + '\'' +
               '}';
    }
}
