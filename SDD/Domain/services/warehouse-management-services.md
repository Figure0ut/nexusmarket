# Warehouse Management Domain Service Specification

## 1. Context & Business Purpose
The **`WarehouseManagementService`** fulfills **OBJ-04** (*"Controlar la información de las bodegas"* and Section 6.1 Step 1). It enables the Administrator to register merchant warehouses and marketplace storage hubs, control physical storage spaces, manage active/inactive operational status, and update warehouse locations.

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`WarehouseManagementService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/WarehouseManagementService.java)
- **Dependencies**: [`WarehouseRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java)
- **Associated Models**: [`Warehouse`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Warehouse.java), [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/User.java), [`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Address.java)

---

## 3. Core Business Invariants & Rules

1. **Admin Authorization Rule**: Only users holding the `ADMIN` role are permitted to register new warehouses in the platform.
2. **Unique Warehouse Identification**: Each warehouse must have a unique identifier across the system.
3. **Operational State Control**: An inactive warehouse cannot be selected for physical stock allocation or logistics dispatch.

---

## 4. Method Signatures

```java
public Warehouse registerWarehouse(Warehouse warehouse, User adminUser);
public Warehouse getWarehouse(String warehouseId);
public void activateWarehouse(String warehouseId);
public void deactivateWarehouse(String warehouseId);
public void updateWarehouseLocation(String warehouseId, Address newLocation);
public List<Warehouse> getWarehousesByOwner(String ownerId);
```
