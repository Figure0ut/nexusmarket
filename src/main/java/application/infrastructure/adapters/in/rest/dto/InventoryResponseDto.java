package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.InventoryMovementType;
import application.domain.models.Inventory;

public class InventoryResponseDto {
    private String inventoryId;
    private String productId;
    private String warehouseId;
    private int availableStock;
    private int reservedStock;
    private int damagedStock;
    private InventoryMovementType lastMovementType;

    public InventoryResponseDto() {}

    public static InventoryResponseDto fromDomain(Inventory inventory) {
        if (inventory == null) return null;
        InventoryResponseDto dto = new InventoryResponseDto();
        dto.setInventoryId(inventory.getInventoryId());
        dto.setProductId(inventory.getProductId());
        dto.setWarehouseId(inventory.getWarehouseId());
        dto.setAvailableStock(inventory.getAvailableStock() != null ? inventory.getAvailableStock().getValue() : 0);
        dto.setReservedStock(inventory.getReservedStock() != null ? inventory.getReservedStock().getValue() : 0);
        dto.setDamagedStock(inventory.getDamagedStock() != null ? inventory.getDamagedStock().getValue() : 0);
        dto.setLastMovementType(inventory.getLastMovementType());
        return dto;
    }

    public String getInventoryId() { return inventoryId; }
    public void setInventoryId(String inventoryId) { this.inventoryId = inventoryId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }

    public int getReservedStock() { return reservedStock; }
    public void setReservedStock(int reservedStock) { this.reservedStock = reservedStock; }

    public int getDamagedStock() { return damagedStock; }
    public void setDamagedStock(int damagedStock) { this.damagedStock = damagedStock; }

    public InventoryMovementType getLastMovementType() { return lastMovementType; }
    public void setLastMovementType(InventoryMovementType lastMovementType) { this.lastMovementType = lastMovementType; }
}
