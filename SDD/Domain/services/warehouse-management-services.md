# Warehouse Management Atomic Domain Services Specification

## 1. Context & Business Purpose
The Warehouse Management domain controls physical storage spaces, marketplace and merchant facilities, activation states, and location management (OBJ-04 & Dominio 4). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`WarehouseRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java)
- **Associated Models**: [`Warehouse`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Warehouse.java), [`WarehouseType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/WarehouseType.java), [`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Address.java)

### Services
1. **`RegisterWarehouseService`**: Registers physical storage facilities requiring `ADMIN` user authorization.
2. **`GetWarehouseService`**: Retrieves a warehouse by its identifier.
3. **`ActivateWarehouseService`**: Activates a warehouse for inventory storage and dispatch.
4. **`DeactivateWarehouseService`**: Deactivates a warehouse.
5. **`UpdateWarehouseLocationService`**: Updates the physical address of a warehouse.
6. **`GetWarehousesByOwnerService`**: Retrieves all warehouses belonging to an owner.

---

## 3. Core Business Invariants & Rules

1. **Admin Authorization**: Only an `ADMIN` user may register a warehouse into the platform.
2. **Active Warehouse Operational Rule**: Inactive warehouses cannot receive stock or dispatch shipments.
3. **Unique Facility ID**: Facility identifiers must be globally unique across the marketplace.

---

## 4. Method Signatures

```java
// RegisterWarehouseService
public Warehouse registerWarehouse(Warehouse warehouse, User adminUser);

// GetWarehouseService
public Warehouse getWarehouse(String warehouseId);

// ActivateWarehouseService
public void activateWarehouse(String warehouseId);

// DeactivateWarehouseService
public void deactivateWarehouse(String warehouseId);

// UpdateWarehouseLocationService
public void updateWarehouseLocation(String warehouseId, Address newLocation);

// GetWarehousesByOwnerService
public List<Warehouse> getWarehousesByOwner(String ownerId);
```
