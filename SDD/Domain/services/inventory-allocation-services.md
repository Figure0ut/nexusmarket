# Inventory Allocation Domain Service Specification

## 1. Context & Business Purpose
The **`InventoryAllocationService`** controls multi-warehouse stock reservation, stock inflows, and inventory movements (OBJ-06 & Dominio 6).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`InventoryAllocationService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/InventoryAllocationService.java)
- **Dependencies**: [`InventoryRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/InventoryRepositoryPort.java), [`WarehouseRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java)
- **Associated Models**: [`Inventory`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Inventory.java), [`Warehouse`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Warehouse.java), [`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/StockQuantity.java)

---

## 3. Core Business Invariants & Rules

1. **Zero Negative Stock Rule**: Negative stock quantities are strictly forbidden under any circumstances.
2. **Damaged / Inactive Warehouse Prohibition**: Stock cannot be allocated to inactive warehouses, nor reserved from damaged units (Validaciones Críticas 11).
3. **Movements**: Inflow, reservation, sales confirmation, reservation release, and damaged stock tracking.

---

## 4. Method Signatures

```java
public Inventory getInventory(String productId, String warehouseId);
public void addStock(String inventoryId, String productId, String warehouseId, int quantity);
public void reserveStock(String productId, String warehouseId, int quantity);
public void releaseReservation(String productId, String warehouseId, int quantity);
public void confirmSale(String productId, String warehouseId, int quantity);
public void markStockAsDamaged(String productId, String warehouseId, int quantity);
public int getTotalAvailableStock(String productId);
```
