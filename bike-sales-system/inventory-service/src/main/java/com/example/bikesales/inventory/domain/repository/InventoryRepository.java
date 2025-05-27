package com.example.bikesales.inventory.domain.repository;

import com.example.bikesales.domain.model.BikeId;
import com.example.bikesales.inventory.domain.model.InventoryItem;
import java.util.Optional;
// import java.util.List; // If findAll is added

public interface InventoryRepository {

    /**
     * Finds an inventory item by its unique bike ID.
     * @param bikeId The ID of the bike.
     * @return An Optional containing the InventoryItem if found, or an empty Optional otherwise.
     */
    Optional<InventoryItem> findByBikeId(BikeId bikeId);

    /**
     * Saves an inventory item (either new or updated).
     * Implementations should handle insert vs update logic.
     * @param item The InventoryItem to save.
     */
    void save(InventoryItem item);

    // Optional additional methods from example:
    // /**
    //  * Finds all inventory items.
    //  * @return A list of all inventory items.
    //  */
    // List<InventoryItem> findAll();
    //
    // /**
    //  * Deletes an inventory item by its bike ID.
    //  * @param bikeId The ID of the bike whose inventory item should be deleted.
    //  */
    // void deleteByBikeId(BikeId bikeId);
}
