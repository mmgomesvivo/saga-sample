package com.example.bikesales.inventory.application.dto;

import com.example.bikesales.domain.model.BikeId; // Assuming BikeId is accessible

public record InventoryItemDto(
    BikeId bikeId,
    int quantityInStock,
    int quantityReserved,
    int availableToReserve
) {
    // Static factory method for easy conversion from domain object
    public static InventoryItemDto fromDomain(com.example.bikesales.inventory.domain.model.InventoryItem item) {
        if (item == null) return null;
        return new InventoryItemDto(
            item.getBikeId(),
            item.getQuantityInStock(),
            item.getQuantityReserved(),
            item.getAvailableToReserve()
        );
    }
}
