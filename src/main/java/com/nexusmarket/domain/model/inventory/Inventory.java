package com.nexusmarket.domain.model.inventory;

import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing distributed inventory linked to a product and warehouse.
 * Enforces zero negative stock invariants and stock movement rules.
 */
public class Inventory {

    private final String inventoryId;
    private final String productId;
    private final String warehouseId;
    private StockQuantity availableStock;
    private StockQuantity reservedStock;
    private StockQuantity damagedStock;

    public Inventory(String inventoryId, String productId, String warehouseId,
                     StockQuantity availableStock, StockQuantity reservedStock, StockQuantity damagedStock) {
        if (inventoryId == null || inventoryId.trim().isEmpty()) {
            throw new IllegalArgumentException("Inventory ID cannot be null or empty.");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }

        this.inventoryId = inventoryId.trim();
        this.productId = productId.trim();
        this.warehouseId = warehouseId.trim();
        this.availableStock = availableStock != null ? availableStock : StockQuantity.zero();
        this.reservedStock = reservedStock != null ? reservedStock : StockQuantity.zero();
        this.damagedStock = damagedStock != null ? damagedStock : StockQuantity.zero();
    }

    public Inventory(String inventoryId, String productId, String warehouseId, int initialStock) {
        this(inventoryId, productId, warehouseId, new StockQuantity(initialStock), StockQuantity.zero(), StockQuantity.zero());
    }

    public void addStock(int quantity) {
        this.availableStock = this.availableStock.add(quantity);
    }

    public void reserveStock(int quantity) {
        if (this.availableStock.getValue() < quantity) {
            throw new IllegalStateException("Stock Reservation Failure: Product '" + productId +
                    "' in Warehouse '" + warehouseId + "' has insufficient available stock (" +
                    availableStock.getValue() + ") for requested reservation (" + quantity + ").");
        }
        this.availableStock = this.availableStock.subtract(quantity);
        this.reservedStock = this.reservedStock.add(quantity);
    }

    public void confirmSale(int quantity) {
        if (this.reservedStock.getValue() < quantity) {
            throw new IllegalStateException("Sale Confirmation Failure: Reserved stock (" +
                    reservedStock.getValue() + ") is less than sale quantity (" + quantity + ").");
        }
        this.reservedStock = this.reservedStock.subtract(quantity);
    }

    public void releaseReservation(int quantity) {
        if (this.reservedStock.getValue() < quantity) {
            throw new IllegalStateException("Reservation Release Failure: Reserved stock (" +
                    reservedStock.getValue() + ") is less than release quantity (" + quantity + ").");
        }
        this.reservedStock = this.reservedStock.subtract(quantity);
        this.availableStock = this.availableStock.add(quantity);
    }

    public void markAsDamaged(int quantity) {
        this.availableStock = this.availableStock.subtract(quantity);
        this.damagedStock = this.damagedStock.add(quantity);
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getProductId() {
        return productId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public StockQuantity getAvailableStock() {
        return availableStock;
    }

    public StockQuantity getReservedStock() {
        return reservedStock;
    }

    public StockQuantity getDamagedStock() {
        return damagedStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(inventoryId, inventory.inventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId);
    }
}
