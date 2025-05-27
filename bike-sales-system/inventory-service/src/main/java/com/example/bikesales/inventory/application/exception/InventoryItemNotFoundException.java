package com.example.bikesales.inventory.application.exception;

public class InventoryItemNotFoundException extends RuntimeException {
    public InventoryItemNotFoundException(String message) {
        super(message);
    }

    public InventoryItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
