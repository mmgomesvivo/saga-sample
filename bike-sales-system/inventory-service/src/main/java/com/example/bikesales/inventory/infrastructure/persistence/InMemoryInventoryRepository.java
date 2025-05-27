package com.example.bikesales.inventory.infrastructure.persistence;

import com.example.bikesales.domain.model.BikeId;
import com.example.bikesales.inventory.domain.model.InventoryItem;
import com.example.bikesales.inventory.domain.repository.InventoryRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInventoryRepository implements InventoryRepository {

    private final Map<BikeId, InventoryItem> inventoryStore = new ConcurrentHashMap<>();

    @Override
    public Optional<InventoryItem> findByBikeId(BikeId bikeId) {
        Objects.requireNonNull(bikeId, "bikeId cannot be null"); // As per instruction
        InventoryItem item = inventoryStore.get(bikeId);
        return Optional.ofNullable(item);
    }

    @Override
    public void save(InventoryItem item) {
        Objects.requireNonNull(item, "InventoryItem cannot be null"); // As per instruction
        Objects.requireNonNull(item.getBikeId(), "InventoryItem's BikeId cannot be null"); // As per instruction
        
        inventoryStore.put(item.getBikeId(), item);
        
        // Logging as per instruction
        System.out.println("InMemoryInventoryRepository: Saved/Updated item for BikeId: " + item.getBikeId().value() +
                           ". Current stock: " + item.getQuantityInStock() +
                           ", Reserved: " + item.getQuantityReserved());
    }

    // Optional helper method from example, can be added if needed for testing setup.
    // public void initializeWithItems(Map<BikeId, InventoryItem> initialItems) {
    //     if (initialItems != null) {
    //         inventoryStore.putAll(initialItems);
    //         System.out.println("InMemoryInventoryRepository: Initialized with " + initialItems.size() + " items.");
    //     }
    // }
}
