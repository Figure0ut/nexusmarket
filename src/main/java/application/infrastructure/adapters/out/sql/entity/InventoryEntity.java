package application.infrastructure.adapters.out.sql.entity;

import application.domain.enums.InventoryMovementType;
import jakarta.persistence.*;

@Entity
@Table(name = "inventories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
})
public class InventoryEntity {

    @Id
    @Column(name = "inventory_id", nullable = false, unique = true)
    private String inventoryId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId;

    @Column(name = "available_stock", nullable = false)
    private int availableStock;

    @Column(name = "reserved_stock", nullable = false)
    private int reservedStock;

    @Column(name = "damaged_stock", nullable = false)
    private int damagedStock;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_movement_type")
    private InventoryMovementType lastMovementType;

    public InventoryEntity() {}

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
