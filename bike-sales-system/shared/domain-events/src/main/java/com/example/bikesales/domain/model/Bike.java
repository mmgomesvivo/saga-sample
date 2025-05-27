package com.example.bikesales.domain.model;

import java.util.Objects;

public class Bike {
    private final BikeId id;
    private final BikeName name;
    private final Price price;
    private final BikeSpecifications specifications;

    public Bike(BikeId id, BikeName name, Price price, BikeSpecifications specifications) {
        if (id == null) {
            throw new IllegalArgumentException("BikeId cannot be null.");
        }
        if (name == null) {
            throw new IllegalArgumentException("BikeName cannot be null.");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null.");
        }
        if (specifications == null) {
            throw new IllegalArgumentException("BikeSpecifications cannot be null.");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.specifications = specifications;
    }

    public Money calculateTotalPrice(Quantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null.");
        }
        return price.multiplyBy(quantity);
    }

    public boolean isAvailable() {
        return specifications.isInStock();
    }

    public BikeId id() {
        return this.id;
    }

    public BikeName name() {
        return this.name;
    }

    // price() and specifications() methods are not strictly required by the current prompt
    // but are included here for completeness, following the pattern of id() and name().
    // They can be removed if they are not used elsewhere in the domain.
    public Price price() {
        return this.price;
    }

    public BikeSpecifications specifications() {
        return this.specifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bike bike = (Bike) o;
        return Objects.equals(id, bike.id) &&
               Objects.equals(name, bike.name) &&
               Objects.equals(price, bike.price) &&
               Objects.equals(specifications, bike.specifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, specifications);
    }

    @Override
    public String toString() {
        return "Bike{" +
               "id=" + id +
               ", name=" + name +
               ", price=" + price +
               ", specifications=" + specifications +
               '}';
    }
}
