package com.example.bikesales.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Price {
    private final BigDecimal amount;
    private final String currency;

    public Price(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price amount cannot be null or negative.");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank.");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currency() {
        return currency;
    }

    public Money multiplyBy(Quantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null.");
        }
        BigDecimal totalAmount = this.amount.multiply(quantity.value());
        return new Money(totalAmount, this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(amount, price.amount) &&
               Objects.equals(currency, price.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "Price{" +
               "amount=" + amount +
               ", currency='" + currency + '\'' +
               '}';
    }
}
