# Warehouse Management Services Specification

## 1. Business Purpose
Domain 4: control of physical storage spaces. The specification distinguishes Marketplace warehouses and Seller warehouses.

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`RegisterWarehouseService`](../../../src/main/java/application/domain/services/RegisterWarehouseService.java) | Registers a warehouse (ADMIN only). |
| [`GetWarehouseService`](../../../src/main/java/application/domain/services/GetWarehouseService.java) | Queries a warehouse. |
| [`GetAllWarehousesService`](../../../src/main/java/application/domain/services/GetAllWarehousesService.java) | Lists warehouses. |
| [`GetWarehousesByOwnerService`](../../../src/main/java/application/domain/services/GetWarehousesByOwnerService.java) | Lists warehouses of an owner. |
| [`ActivateWarehouseService`](../../../src/main/java/application/domain/services/ActivateWarehouseService.java) | Activates. |
| [`DeactivateWarehouseService`](../../../src/main/java/application/domain/services/DeactivateWarehouseService.java) | Deactivates. |
| [`UpdateWarehouseLocationService`](../../../src/main/java/application/domain/services/UpdateWarehouseLocationService.java) | Updates the location. |

## 3. Business Rules Enforced

1. `MARKETPLACE` warehouses are owned by the marketplace (`ownerId = NEXUSMARKET`).
2. `SELLER` warehouses must belong to an existing user with role `SELLER`.
3. Inactive warehouses cannot receive stock, reserve stock or dispatch.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/warehouses` | ADMIN |
| `GET` | `/api/warehouses` | ADMIN, SUPERVISOR, OPERATOR_LOGISTIC, SELLER (marketplace + own) |
| `GET` | `/api/warehouses/{id}` | ADMIN, SUPERVISOR, OPERATOR_LOGISTIC, SELLER |
| `GET` | `/api/warehouses/owner/{ownerId}` | same; a seller only its own |
| `PUT` | `/api/warehouses/{id}/activate · /deactivate · /location` | ADMIN |
