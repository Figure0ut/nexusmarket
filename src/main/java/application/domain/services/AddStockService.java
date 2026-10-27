package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.StockQuantity;

/**
 * Atomic domain service responsible exclusively for adding stock to an inventory record.
 */
public class AddStockService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public AddStockService(InventoryRepositoryPort inventoryRepositoryPort, WarehouseRepositoryPort warehouseRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public void addStock(String inventoryId, String productId, String warehouseId, int quantity) {
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));

        if (!warehouse.isActive()) {
            throw new InvalidDomainStateException("Cannot allocate stock to inactive warehouse '" + warehouseId + "'.");
        }

        Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(productId, warehouseId)
                .orElseGet(() -> new Inventory(inventoryId, productId, warehouseId, StockQuantity.zero(), StockQuantity.zero(), StockQuantity.zero()));

        inventory.addStock(quantity);
        inventoryRepositoryPort.save(inventory);
    }
}
