package com.example.bikesales.inventory.application.dto;

import com.example.bikesales.domain.model.BikeId; // Assuming BikeId is accessible
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object representing the inventory status of a bike.")
public record InventoryItemDto(
    @Schema(description = "The unique identifier of the bike.", requiredMode = Schema.RequiredMode.REQUIRED)
    BikeId bikeId,
    @Schema(description = "Total quantity currently in stock.", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    int quantityInStock,
    @Schema(description = "Quantity currently reserved (e.g., in active carts or pending orders).", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    int quantityReserved,
    @Schema(description = "Quantity available for new reservations (quantityInStock - quantityReserved).", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
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
