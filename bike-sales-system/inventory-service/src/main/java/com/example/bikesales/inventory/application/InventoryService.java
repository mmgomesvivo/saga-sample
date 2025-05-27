package com.example.bikesales.inventory.application;

import com.example.bikesales.domain.model.BikeId;
import com.example.bikesales.inventory.application.dto.InventoryItemDto;
import com.example.bikesales.inventory.application.exception.InventoryItemNotFoundException;
import com.example.bikesales.inventory.domain.model.InventoryItem;
import com.example.bikesales.inventory.domain.repository.InventoryRepository;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository, "InventoryRepository cannot be null.");
    }

    public CompletableFuture<Boolean> reserveStock(BikeId bikeId, int quantityToReserve) {
        Objects.requireNonNull(bikeId, "bikeId cannot be null");
        if (quantityToReserve <= 0) {
            System.err.println("InventoryService: Quantity to reserve must be positive. Received: " + quantityToReserve);
            return CompletableFuture.completedFuture(false);
        }

        Optional<InventoryItem> itemOptional = inventoryRepository.findByBikeId(bikeId);
        if (itemOptional.isEmpty()) {
            System.err.println("InventoryService: Attempted to reserve stock for non-existent bikeId: " + bikeId.value());
            return CompletableFuture.completedFuture(false);
        }

        InventoryItem item = itemOptional.get();
        try {
            item.reserveStock(quantityToReserve); // Domain logic
            inventoryRepository.save(item);       // Persist change
            System.out.println("InventoryService: Stock reserved for " + bikeId.value() +
                               ". New reserved: " + item.getQuantityReserved() +
                               ", Available to reserve: " + item.getAvailableToReserve());
            return CompletableFuture.completedFuture(true);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("InventoryService: Failed to reserve stock for " + bikeId.value() +
                               ". Quantity: " + quantityToReserve + ". Reason: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    public CompletableFuture<Void> releaseStockReservation(BikeId bikeId, int quantityToRelease) {
        Objects.requireNonNull(bikeId, "bikeId cannot be null");
        if (quantityToRelease <= 0) {
            System.err.println("InventoryService: Quantity to release must be positive. Received: " + quantityToRelease);
            // Or throw new IllegalArgumentException("Quantity to release must be positive.");
            // For consistency with example, let's log and then potentially throw if not found
        }

        InventoryItem item = inventoryRepository.findByBikeId(bikeId)
            .orElseThrow(() -> {
                String errorMsg = "Inventory item not found for bike: " + bikeId.value() + ". Cannot release stock.";
                System.err.println("InventoryService: " + errorMsg);
                return new InventoryItemNotFoundException(errorMsg);
            });

        try {
            item.releaseStockReservation(quantityToRelease);
            inventoryRepository.save(item);
            System.out.println("InventoryService: Stock reservation released for " + bikeId.value() +
                               ". Quantity released: " + quantityToRelease +
                               ". New reserved: " + item.getQuantityReserved());
            return CompletableFuture.completedFuture(null);
        } catch (IllegalArgumentException | IllegalStateException e) { // Catching potential domain exceptions
            System.err.println("InventoryService: Failed to release stock reservation for " + bikeId.value() +
                               ". Quantity: " + quantityToRelease + ". Reason: " + e.getMessage());
            // Rethrow as a new runtime exception or a specific application exception
            throw new RuntimeException("Failed to release stock: " + e.getMessage(), e);
        }
    }

    public CompletableFuture<Void> confirmStockReservation(BikeId bikeId, int quantityToConfirm) {
        Objects.requireNonNull(bikeId, "bikeId cannot be null");
        if (quantityToConfirm <= 0) {
            System.err.println("InventoryService: Quantity to confirm must be positive. Received: " + quantityToConfirm);
            // Or throw new IllegalArgumentException("Quantity to confirm must be positive.");
        }
        
        InventoryItem item = inventoryRepository.findByBikeId(bikeId)
            .orElseThrow(() -> {
                String errorMsg = "Inventory item not found for bike: " + bikeId.value() + ". Cannot confirm stock.";
                System.err.println("InventoryService: " + errorMsg);
                return new InventoryItemNotFoundException(errorMsg);
            });

        try {
            item.confirmStockReservation(quantityToConfirm);
            inventoryRepository.save(item);
            System.out.println("InventoryService: Stock reservation confirmed for " + bikeId.value() +
                               ". Quantity confirmed: " + quantityToConfirm +
                               ". New stock: " + item.getQuantityInStock() +
                               ", New reserved: " + item.getQuantityReserved());
            return CompletableFuture.completedFuture(null);
        } catch (IllegalArgumentException | IllegalStateException e) { // Catching potential domain exceptions
            System.err.println("InventoryService: Failed to confirm stock reservation for " + bikeId.value() +
                               ". Quantity: " + quantityToConfirm + ". Reason: " + e.getMessage());
            throw new RuntimeException("Failed to confirm stock: " + e.getMessage(), e);
        }
    }

    public CompletableFuture<Void> restockItem(BikeId bikeId, int quantityToAdd) {
        Objects.requireNonNull(bikeId, "bikeId cannot be null");
        if (quantityToAdd <= 0) { // Domain model also checks this, but good for early exit.
            System.err.println("InventoryService: Quantity to add must be positive. Received: " + quantityToAdd);
             // Or throw new IllegalArgumentException("Quantity to add must be positive.");
             // For this method, let's allow the domain object to throw if it's more appropriate,
             // or handle it here consistently. The domain object will throw.
        }

        InventoryItem item = inventoryRepository.findByBikeId(bikeId)
            .orElseGet(() -> {
                System.out.println("InventoryService: No existing inventory item for bike " + bikeId.value() + ". Creating new item.");
                return new InventoryItem(bikeId, 0); // Initial stock is 0, addToStock will increment.
            });

        try {
            item.addToStock(quantityToAdd);
            inventoryRepository.save(item);
            System.out.println("InventoryService: Item restocked for " + bikeId.value() +
                               ". Quantity added: " + quantityToAdd +
                               ". New stock: " + item.getQuantityInStock());
            return CompletableFuture.completedFuture(null);
        } catch (IllegalArgumentException e) { // Catching potential domain exceptions (e.g. non-positive quantity)
             System.err.println("InventoryService: Failed to restock item for " + bikeId.value() +
                               ". Quantity: " + quantityToAdd + ". Reason: " + e.getMessage());
            throw new RuntimeException("Failed to restock item: " + e.getMessage(), e);
        }
    }

    public CompletableFuture<Optional<InventoryItemDto>> getInventoryItemDetails(BikeId bikeId) {
        Objects.requireNonNull(bikeId, "bikeId cannot be null");
        Optional<InventoryItem> itemOpt = inventoryRepository.findByBikeId(bikeId);
        // Log if item not found, as per example's spirit for other methods
        if (itemOpt.isEmpty()) {
            System.out.println("InventoryService: No inventory details found for bikeId: " + bikeId.value());
        }
        return CompletableFuture.completedFuture(itemOpt.map(InventoryItemDto::fromDomain));
    }
}
