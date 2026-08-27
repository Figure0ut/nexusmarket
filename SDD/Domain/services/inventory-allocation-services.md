# Inventory Allocation Domain Service Specification

## 1. Context & Business Purpose
The **Inventory Allocation Domain Service** controls multi-warehouse stock reservation, stock inflows, and inventory movements (OBJ-06 & Dominio 6).

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.inventory`
- **Associated Aggregate Roots**: [`Inventory`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/inventory/Inventory.java), [`Warehouse`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/warehouse/Warehouse.java)
- **Associated Value Objects**: [`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/inventory/StockQuantity.java), [`InventoryMovementType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/inventory/InventoryMovementType.java)

---

## 3. Core Business Invariants & Rules

1. **Zero Negative Stock Rule**: Negative stock quantities are strictly forbidden under any circumstances.
2. **Damaged / Non-Existent Stock Prohibition**: Stock marked as damaged or non-existent cannot be reserved or sold (Validaciones Críticas 11).
3. **Movement Tracking**:
   - `INFLOW`: Increases `availableStock`.
   - `RESERVE`: Transfers units from `availableStock` to `reservedStock`.
   - `OUTFLOW_SALE`: Deducts units from `reservedStock` upon checkout payment.
   - `ADJUSTMENT`: Reconciles inventory stock count.
   - `RETURN`: Adds inspected returned units back to stock.

---

## 4. Key Method Validation Logic

```java
public void reserveStock(int quantity) {
    if (this.availableStock.getValue() < quantity) {
        throw new IllegalStateException("Insufficient available stock for reservation.");
    }
    this.availableStock = this.availableStock.subtract(quantity);
    this.reservedStock = this.reservedStock.add(quantity);
}
```
