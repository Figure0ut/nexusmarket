package application.domain.services;

import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for calculating total available stock of a product across all warehouses.
 */
public class GetTotalAvailableStockService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public GetTotalAvailableStockService(InventoryRepositoryPort inventoryRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public int getTotalAvailableStock(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        List<Inventory> inventories = inventoryRepositoryPort.findByProductId(productId.trim());
        int total = 0;
        for (Inventory inv : inventories) {
            total += inv.getAvailableStock().getValue();
        }
        return total;
    }
}
