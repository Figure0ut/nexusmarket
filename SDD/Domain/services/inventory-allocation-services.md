# Inventory Services Specification

## 1. Business Purpose
Domain 6: distributed inventory, always linked to one product and one warehouse.

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`AddStockService`](../../../src/main/java/application/domain/services/AddStockService.java) | INFLOW movement; creates the inventory record `INV-{product}-{warehouse}` if needed. |
| [`MarkStockDamagedService`](../../../src/main/java/application/domain/services/MarkStockDamagedService.java) | ADJUSTMENT movement: available → damaged. |
| [`GetInventoryService`](../../../src/main/java/application/domain/services/GetInventoryService.java) | Inventory of a product in a warehouse. |
| [`GetInventoriesByProductService`](../../../src/main/java/application/domain/services/GetInventoriesByProductService.java) | Inventory of a product in all warehouses. |
| [`GetTotalAvailableStockService`](../../../src/main/java/application/domain/services/GetTotalAvailableStockService.java) | Total available units. |

## 3. Business Rules Enforced

1. Movements: `INFLOW` (add), `RESERVE` (checkout), `OUTFLOW_SALE` (dispatch), `ADJUSTMENT` (damaged), `RETURN` (refund processing).
2. Negative stock is never allowed (`StockQuantity` and `Inventory` guards).
3. Inexistent or damaged inventory can never be reserved.
4. Digital products have no inventory.
5. A seller only manages stock of its own products and never in another seller's warehouse; buyers never manage inventory.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/inventory/add` | SELLER (own products), OPERATOR_LOGISTIC |
| `POST` | `/api/inventory/mark-damaged` | SELLER (own products), OPERATOR_LOGISTIC |
| `GET` | `/api/inventory?productId=&warehouseId=` | SELLER (own), OPERATOR_LOGISTIC, SUPERVISOR |
| `GET` | `/api/inventory/product/{id} · /total-available/{id}` | same |
