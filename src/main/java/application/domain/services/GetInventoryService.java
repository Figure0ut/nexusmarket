package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying an inventory record by product and warehouse.
 */
public class GetInventoryService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public GetInventoryService(InventoryRepositoryPort inventoryRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public Inventory getInventory(String productId, String warehouseId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        return inventoryRepositoryPort.findByProductAndWarehouse(productId.trim(), warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product '" + productId + "' in warehouse '" + warehouseId + "'."));
    }
}
