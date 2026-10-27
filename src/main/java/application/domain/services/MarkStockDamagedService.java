package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

/**
 * Atomic domain service responsible exclusively for marking stock as damaged in inventory.
 */
public class MarkStockDamagedService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public MarkStockDamagedService(InventoryRepositoryPort inventoryRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public void markStockAsDamaged(String productId, String warehouseId, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(productId.trim(), warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product '" + productId + "' in warehouse '" + warehouseId + "'."));

        inventory.markAsDamaged(quantity);
        inventoryRepositoryPort.save(inventory);
    }
}
