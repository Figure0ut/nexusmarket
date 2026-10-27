package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

/**
 * Atomic domain service responsible exclusively for releasing reserved stock back to available stock.
 */
public class ReleaseStockReservationService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public ReleaseStockReservationService(InventoryRepositoryPort inventoryRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public void releaseReservation(String productId, String warehouseId, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(productId.trim(), warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product '" + productId + "' in warehouse '" + warehouseId + "'."));

        inventory.releaseReservation(quantity);
        inventoryRepositoryPort.save(inventory);
    }
}
