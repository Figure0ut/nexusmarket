package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.StockQuantity;

/**
 * Atomic domain service responsible exclusively for registering stock inflows (INFLOW movement).
 * Inventory is always linked to a specific product and a specific warehouse (Domain 6),
 * and only physical products require inventory (Domain 5).
 */
public class AddStockService {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public AddStockService(InventoryRepositoryPort inventoryRepositoryPort,
                           WarehouseRepositoryPort warehouseRepositoryPort,
                           ProductRepositoryPort productRepositoryPort) {
        if (inventoryRepositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    public Inventory addStock(String productId, String warehouseId, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        Product product = productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        if (!product.isPhysical()) {
            throw new InvalidDomainStateException("Product '" + productId + "' is DIGITAL; digital products do not require inventory.");
        }
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
        if (!warehouse.isActive()) {
            throw new InvalidDomainStateException("Cannot allocate stock to inactive warehouse '" + warehouseId + "'.");
        }
        Inventory inventory = inventoryRepositoryPort.findByProductAndWarehouse(product.getProductId(), warehouse.getWarehouseId())
                .orElseGet(() -> new Inventory(buildInventoryId(product.getProductId(), warehouse.getWarehouseId()),
                        product.getProductId(), warehouse.getWarehouseId(),
                        StockQuantity.zero(), StockQuantity.zero(), StockQuantity.zero()));
        inventory.addStock(quantity);
        return inventoryRepositoryPort.save(inventory);
    }

    public static String buildInventoryId(String productId, String warehouseId) {
        return "INV-" + productId + "-" + warehouseId;
    }
}
