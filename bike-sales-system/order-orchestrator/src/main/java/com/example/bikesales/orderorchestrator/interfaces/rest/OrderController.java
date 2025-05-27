package com.example.bikesales.orderorchestrator.interfaces.rest;

import com.example.bikesales.orderorchestrator.application.OrderPlacementService;
import com.example.bikesales.orderorchestrator.domain.PlaceOrderCommand;
import com.example.bikesales.orderorchestrator.domain.OrderSummaryDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // For @RestController, @RequestMapping, @PostMapping, @RequestBody

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Placement", description = "API for placing new customer orders")
public class OrderController {

    private final OrderPlacementService orderPlacementService;

    public OrderController(OrderPlacementService orderPlacementService) {
        this.orderPlacementService = Objects.requireNonNull(orderPlacementService, "OrderPlacementService cannot be null.");
    }

    @PostMapping // Equivalent to @PostMapping("/")
    @Operation(summary = "Place a new order", description = "Receives order details and initiates the order placement and checkout process asynchronously.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Order placement initiated successfully",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderSummaryDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data provided for the order",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderSummaryDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error during order placement",
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderSummaryDto.class)))
    })
    public CompletableFuture<ResponseEntity<OrderSummaryDto>> placeNewOrder(@RequestBody(required = false) PlaceOrderCommand command) {
        // Using the more robust validation from the example
        if (command == null) {
            OrderSummaryDto errorDto = new OrderSummaryDto(null, "VALIDATION_ERROR", "Request body (PlaceOrderCommand) cannot be null.");
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(errorDto));
        }
        // Additional checks for fields can be done here if command constructor doesn't cover all,
        // or if @Valid is used on @RequestBody with validation annotations in PlaceOrderCommand.
        // For now, PlaceOrderCommand's constructor handles its own validation.

        try {
            // This call might throw IllegalArgumentException if command's constructor validation fails
            // (e.g., if items list is empty, though @RequestBody mapping might fail earlier for malformed JSON).
            // The example's specific catch for IllegalArgumentException handles this.
            // The call to orderPlacementService.placeOrder itself also does null checks on command and its fields.

            return orderPlacementService.placeOrder(command)
                .thenApply(orderSummaryDto -> {
                    // Using example's logging for success
                    System.out.println("OrderController: Successfully initiated order placement. Returning 202 Accepted.");
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderSummaryDto);
                })
                .exceptionally(ex -> {
                    // Using example's more detailed exception handling
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    System.err.println("OrderController: Failed to place order. Error: " + cause.getMessage());
                    OrderSummaryDto errorResult = new OrderSummaryDto(
                        null,
                        "ORDER_PLACEMENT_FAILED",
                        "Internal error occurred while placing the order: " + cause.getMessage()
                    );
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
                });
        } catch (IllegalArgumentException e) {
             // Catch potential errors from PlaceOrderCommand constructor or service's initial synchronous checks
             System.err.println("OrderController: Invalid PlaceOrderCommand provided. Error: " + e.getMessage());
             OrderSummaryDto errorResult = new OrderSummaryDto(null, "VALIDATION_ERROR", e.getMessage());
             return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(errorResult));
        } catch (Exception e) { // Catch any other unexpected synchronous errors
            System.err.println("OrderController: Unexpected error during order placement setup. Error: " + e.getMessage());
            OrderSummaryDto errorResult = new OrderSummaryDto(
                null,
                "ORDER_PLACEMENT_UNEXPECTED_ERROR",
                "An unexpected error occurred: " + e.getMessage()
            );
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult));
        }
    }
}
