package application.domain.models;

import application.domain.enums.InventoryMovementType;
import application.domain.valueobjects.StockQuantity;

import java.util.Objects;

public class Inventory {

    private String inventoryId;
    private String productId;
    private String warehouseId;
    private StockQuantity availableStock;
    private StockQuantity reservedStock;
    private StockQuantity damagedStock;
    private InventoryMovementType lastMovementType;

    public Inventory() {
        this.availableStock = StockQuantity.zero();
        this.reservedStock = StockQuantity.zero();
        this.damagedStock = StockQuantity.zero();
    }

    public Inventory(String inventoryId, String productId, String warehouseId,
                     StockQuantity availableStock, StockQuantity reservedStock, StockQuantity damagedStock) {
        setInventoryId(inventoryId);
        setProductId(productId);
        setWarehouseId(warehouseId);
        setAvailableStock(availableStock);
        setReservedStock(reservedStock);
        setDamagedStock(damagedStock);
    }

    public Inventory(String inventoryId, String productId, String warehouseId, int initialStock) {
        this(inventoryId, productId, warehouseId, new StockQuantity(initialStock), StockQuantity.zero(), StockQuantity.zero());
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        if (inventoryId == null || inventoryId.trim().isEmpty()) {
            throw new IllegalArgumentException("Inventory ID cannot be null or empty.");
        }
        this.inventoryId = inventoryId.trim();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        this.productId = productId.trim();
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        this.warehouseId = warehouseId.trim();
    }

    public StockQuantity getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(StockQuantity availableStock) {
        this.availableStock = availableStock != null ? availableStock : StockQuantity.zero();
    }

    public StockQuantity getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(StockQuantity reservedStock) {
        this.reservedStock = reservedStock != null ? reservedStock : StockQuantity.zero();
    }

    public StockQuantity getDamagedStock() {
        return damagedStock;
    }

    public void setDamagedStock(StockQuantity damagedStock) {
        this.damagedStock = damagedStock != null ? damagedStock : StockQuantity.zero();
    }

    /**
     * Returns the type of the last inventory movement performed on this record.
     *
     * @return the last movement type, or null if no movement has occurred
     */
    public InventoryMovementType getLastMovementType() {
        return lastMovementType;
    }

    public void setLastMovementType(InventoryMovementType lastMovementType) {
        this.lastMovementType = lastMovementType;
    }

    /**
     * Adds stock to available inventory (INFLOW movement).
     *
     * @param quantity the number of units to add
     */
    public void addStock(int quantity) {
        this.availableStock = this.availableStock.add(quantity);
        this.lastMovementType = InventoryMovementType.INFLOW;
    }

    /**
     * Reserves available stock for a pending order (RESERVE movement).
     *
     * @param quantity the number of units to reserve
     * @throws IllegalStateException if insufficient available stock
     */
    public void reserveStock(int quantity) {
        if (this.availableStock.getValue() < quantity) {
            throw new IllegalStateException("Stock Reservation Failure: Product '" + productId +
                    "' in Warehouse '" + warehouseId + "' has insufficient available stock (" +
                    availableStock.getValue() + ") for requested reservation (" + quantity + ").");
        }
        this.availableStock = this.availableStock.subtract(quantity);
        this.reservedStock = this.reservedStock.add(quantity);
        this.lastMovementType = InventoryMovementType.RESERVE;
    }

    /**
     * Confirms a sale by removing reserved stock (OUTFLOW_SALE movement).
     *
     * @param quantity the number of units sold
     * @throws IllegalStateException if reserved stock is insufficient
     */
    public void confirmSale(int quantity) {
        if (this.reservedStock.getValue() < quantity) {
            throw new IllegalStateException("Sale Confirmation Failure: Reserved stock (" +
                    reservedStock.getValue() + ") is less than sale quantity (" + quantity + ").");
        }
        this.reservedStock = this.reservedStock.subtract(quantity);
        this.lastMovementType = InventoryMovementType.OUTFLOW_SALE;
    }

    /**
     * Releases a reservation, returning stock to available (RETURN movement).
     *
     * @param quantity the number of units to release
     * @throws IllegalStateException if reserved stock is insufficient
     */
    public void releaseReservation(int quantity) {
        if (this.reservedStock.getValue() < quantity) {
            throw new IllegalStateException("Reservation Release Failure: Reserved stock (" +
                    reservedStock.getValue() + ") is less than release quantity (" + quantity + ").");
        }
        this.reservedStock = this.reservedStock.subtract(quantity);
        this.availableStock = this.availableStock.add(quantity);
        this.lastMovementType = InventoryMovementType.RETURN;
    }

    /**
     * Marks available stock as damaged (ADJUSTMENT movement).
     *
     * @param quantity the number of units to mark as damaged
     */
    public void markAsDamaged(int quantity) {
        this.availableStock = this.availableStock.subtract(quantity);
        this.damagedStock = this.damagedStock.add(quantity);
        this.lastMovementType = InventoryMovementType.ADJUSTMENT;
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
