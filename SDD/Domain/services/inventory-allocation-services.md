# Inventory & Stock Allocation Atomic Domain Services Specification

## 1. Context & Business Purpose
The Inventory domain coordinates multi-warehouse stock allocation, real-time reservations, sale confirmations, damage adjustments, and movement audits (OBJ-05 & Dominio 5). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`InventoryRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/InventoryRepositoryPort.java), [`WarehouseRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java)
- **Associated Models**: [`Inventory`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Inventory.java), [`Warehouse`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Warehouse.java), [`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/StockQuantity.java), [`InventoryMovementType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/InventoryMovementType.java)

### Services
1. **`GetInventoryService`**: Retrieves inventory by product and warehouse.
2. **`AddStockService`**: Adds stock to active warehouse inventory (`INFLOW`).
3. **`ReserveStockService`**: Reserves stock for pending checkout orders (`RESERVE`).
4. **`ReleaseStockReservationService`**: Returns reserved stock to available (`RETURN`).
5. **`ConfirmSaleStockService`**: Deducts reserved stock upon confirmed sale (`OUTFLOW_SALE`).
6. **`MarkStockDamagedService`**: Moves stock to damaged classification (`ADJUSTMENT`).
7. **`GetTotalAvailableStockService`**: Computes total available stock across all warehouses.

---

## 3. Core Business Invariants & Rules

1. **Zero Negative Stock Rule**: Available, reserved, and damaged stock levels cannot be negative ($stock \ge 0$).
2. **Active Warehouse Invariant**: Stock can only be added to or dispatched from `active` warehouses.
3. **Movement Tracking**: Every stock mutation automatically records its corresponding `InventoryMovementType`.

---

## 4. Method Signatures

```java
// GetInventoryService
public Inventory getInventory(String productId, String warehouseId);

// AddStockService
public void addStock(String inventoryId, String productId, String warehouseId, int quantity);

// ReserveStockService
public void reserveStock(String productId, String warehouseId, int quantity);

// ReleaseStockReservationService
public void releaseReservation(String productId, String warehouseId, int quantity);

// ConfirmSaleStockService
public void confirmSale(String productId, String warehouseId, int quantity);

// MarkStockDamagedService
public void markStockAsDamaged(String productId, String warehouseId, int quantity);

// GetTotalAvailableStockService
public int getTotalAvailableStock(String productId);
```
