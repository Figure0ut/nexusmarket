package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.StockQuantity;

import java.util.List;

public class InventoryAllocationService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public InventoryAllocationService(InventoryRepositoryPort inventoryRepositoryPort, WarehouseRepositoryPort warehouseRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public Inventory getInventory(String productId, String warehouseId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        return inventoryRepositoryPort.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product '" + productId + "' in warehouse '" + warehouseId + "'."));
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

    public void reserveStock(String productId, String warehouseId, int quantity) {
        Inventory inventory = getInventory(productId, warehouseId);
        inventory.reserveStock(quantity);
        inventoryRepositoryPort.save(inventory);
    }

    public void releaseReservation(String productId, String warehouseId, int quantity) {
        Inventory inventory = getInventory(productId, warehouseId);
        inventory.releaseReservation(quantity);
        inventoryRepositoryPort.save(inventory);
    }

    public void confirmSale(String productId, String warehouseId, int quantity) {
        Inventory inventory = getInventory(productId, warehouseId);
        inventory.confirmSale(quantity);
        inventoryRepositoryPort.save(inventory);
    }

    public void markStockAsDamaged(String productId, String warehouseId, int quantity) {
        Inventory inventory = getInventory(productId, warehouseId);
        inventory.markAsDamaged(quantity);
        inventoryRepositoryPort.save(inventory);
    }

    public int getTotalAvailableStock(String productId) {
        List<Inventory> inventories = inventoryRepositoryPort.findByProductId(productId);
        int total = 0;
        for (Inventory inv : inventories) {
            total += inv.getAvailableStock().getValue();
        }
        return total;
    }
}
