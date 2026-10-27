package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

/**
 * Atomic domain service responsible exclusively for confirming a sale by deducting reserved stock.
 */
public class ConfirmSaleStockService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public ConfirmSaleStockService(InventoryRepositoryPort inventoryRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public void confirmSale(String productId, String warehouseId, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(productId.trim(), warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product '" + productId + "' in warehouse '" + warehouseId + "'."));

        inventory.confirmSale(quantity);
        inventoryRepositoryPort.save(inventory);
    }
}
