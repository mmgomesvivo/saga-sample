package com.example.bikesales.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class BikeId {
    private final UUID value;

    public BikeId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("BikeId value cannot be null.");
        }
        this.value = value;
    }

    public static BikeId generate() {
        return new BikeId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BikeId bikeId = (BikeId) o;
        return Objects.equals(value, bikeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BikeId{" +
               "value=" + value +
               '}';
    }
}
