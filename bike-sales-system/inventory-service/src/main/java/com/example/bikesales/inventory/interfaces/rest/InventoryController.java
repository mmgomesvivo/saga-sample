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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing bike stock levels and reservations")
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
    @Operation(summary = "Get inventory details for a bike", description = "Retrieves current stock, reserved, and available quantities for a specific bike ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory details retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryItemDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid BikeId UUID format", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))), // Assuming error response is plain text or simple JSON
        @ApiResponse(responseCode = "404", description = "Inventory item not found for the given BikeId", content = @Content) // No specific content for 404
    })
    public CompletableFuture<ResponseEntity<InventoryItemDto>> getInventoryItem(@PathVariable String bikeIdStr) {
        BikeId bikeId = toBikeId(bikeIdStr);
        return inventoryService.getInventoryItemDetails(bikeId)
            .thenApply(optionalDto -> optionalDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping("/{bikeIdStr}/reserve")
    @Operation(summary = "Reserve stock for a bike", description = "Attempts to reserve a specified quantity of a bike. This is typically part of a SAGA.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock reserved successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid input (e.g., non-positive quantity, item not found, or not enough stock)", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
    })
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
    @Operation(summary = "Release stock reservation for a bike", description = "Releases a previously made stock reservation.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock reservation released successfully", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input (e.g., non-positive quantity or other processing error)", content = @Content),
        @ApiResponse(responseCode = "404", description = "Inventory item not found for the given BikeId", content = @Content)
    })
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
    @Operation(summary = "Confirm stock reservation for a bike", description = "Confirms a reservation, typically leading to decrementing actual stock.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock reservation confirmed successfully", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input (e.g., non-positive quantity or other processing error)", content = @Content),
        @ApiResponse(responseCode = "404", description = "Inventory item not found for the given BikeId", content = @Content)
    })
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
    @Operation(summary = "Restock an inventory item", description = "Adds a specified quantity to the stock of a bike.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item restocked successfully", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input (e.g., non-positive quantity or other processing error)", content = @Content)
        // 404 is less likely here as restockItem often creates the item if not found,
        // but can be added if the service throws InventoryItemNotFoundException for restock on non-existing.
    })
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
