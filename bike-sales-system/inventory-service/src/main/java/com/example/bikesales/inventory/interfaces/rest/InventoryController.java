package com.example.bikesales.inventory.interfaces.rest;

import com.example.bikesales.domain.model.BikeId;
import com.example.bikesales.inventory.application.InventoryService;
import com.example.bikesales.inventory.application.dto.InventoryItemDto;
import com.example.bikesales.inventory.application.exception.InventoryItemNotFoundException;
import com.example.bikesales.inventory.interfaces.rest.dto.QuantityRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = Objects.requireNonNull(inventoryService, "InventoryService cannot be null.");
    }

    private BikeId toBikeId(String uuidStr) {
        try {
            return new BikeId(UUID.fromString(uuidStr));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid BikeId UUID format: " + uuidStr, e);
        }
    }

    @GetMapping("/{bikeIdStr}")
    public CompletableFuture<ResponseEntity<InventoryItemDto>> getInventoryItem(@PathVariable String bikeIdStr) {
        BikeId bikeId = toBikeId(bikeIdStr);
        return inventoryService.getInventoryItemDetails(bikeId)
            .thenApply(optionalDto -> optionalDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping("/{bikeIdStr}/reserve")
    public CompletableFuture<ResponseEntity<String>> reserveStock(@PathVariable String bikeIdStr, @RequestBody QuantityRequest request) {
        BikeId bikeId = toBikeId(bikeIdStr);
        if (request.quantity() <= 0) {
            // Instructions mention validating quantity > 0.
            // Returning a specific error for this before calling service.
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body("Quantity to reserve must be positive.")
            );
        }
        return inventoryService.reserveStock(bikeId, request.quantity())
            .thenApply(success -> success
                ? ResponseEntity.ok("Stock reserved successfully for " + bikeIdStr)
                : ResponseEntity.badRequest().body("Failed to reserve stock for " + bikeIdStr + ". Not enough stock or item not found."));
    }

    @PostMapping("/{bikeIdStr}/release")
    public CompletableFuture<ResponseEntity<Void>> releaseStock(@PathVariable String bikeIdStr, @RequestBody QuantityRequest request) {
        BikeId bikeId = toBikeId(bikeIdStr);
        if (request.quantity() <= 0) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }
        return inventoryService.releaseStockReservation(bikeId, request.quantity())
            .thenRun(() -> ResponseEntity.ok().<Void>build()) // Use <Void>build() for clarity
            .exceptionally(ex -> {
                // Handle specific exceptions, e.g., InventoryItemNotFoundException
                if (ex.getCause() instanceof InventoryItemNotFoundException) {
                    return ResponseEntity.notFound().build();
                }
                // Handle other exceptions from service (e.g., rethrown RuntimeException)
                System.err.println("Error releasing stock for " + bikeIdStr + ": " + ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).<Void>build();
            });
    }

    @PostMapping("/{bikeIdStr}/confirm")
    public CompletableFuture<ResponseEntity<Void>> confirmStock(@PathVariable String bikeIdStr, @RequestBody QuantityRequest request) {
        BikeId bikeId = toBikeId(bikeIdStr);
         if (request.quantity() <= 0) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }
        return inventoryService.confirmStockReservation(bikeId, request.quantity())
            .thenRun(() -> ResponseEntity.ok().<Void>build())
            .exceptionally(ex -> {
                if (ex.getCause() instanceof InventoryItemNotFoundException) {
                    return ResponseEntity.notFound().build();
                }
                System.err.println("Error confirming stock for " + bikeIdStr + ": " + ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).<Void>build();
            });
    }

    @PostMapping("/{bikeIdStr}/restock")
    public CompletableFuture<ResponseEntity<Void>> restockItem(@PathVariable String bikeIdStr, @RequestBody QuantityRequest request) {
        BikeId bikeId = toBikeId(bikeIdStr);
         if (request.quantity() <= 0) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }
        return inventoryService.restockItem(bikeId, request.quantity())
            .thenRun(() -> ResponseEntity.ok().<Void>build())
            .exceptionally(ex -> { // Catching potential RuntimeException from service
                System.err.println("Error restocking item " + bikeIdStr + ": " + ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).<Void>build();
            });
    }
}
