# Logistics Services Specification

## 1. Business Purpose
OBJ-10: packing, dispatch and transport of physical products (section 6.1, steps 7 and 8).

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`PrepareShipmentService`](../../../src/main/java/application/domain/services/PrepareShipmentService.java) | Creates the shipment (`PREPARING`) of a PAID order with physical items, from the warehouse where stock was reserved, to the order shipping address. |
| [`DispatchShipmentService`](../../../src/main/java/application/domain/services/DispatchShipmentService.java) | `PREPARING` → `IN_TRANSIT`; order PAID → DISPATCHED; registers OUTFLOW_SALE for every physical item. |
| [`ConfirmDeliveryService`](../../../src/main/java/application/domain/services/ConfirmDeliveryService.java) | `IN_TRANSIT` → `DELIVERED`; order → DELIVERED_FINALIZED. |
| [`GetShipmentService`](../../../src/main/java/application/domain/services/GetShipmentService.java) | Queries a shipment. |

## 3. Business Rules Enforced

1. Shipments only exist for physical products.
2. One shipment per order.
3. Executed by the Logistics Operator.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/shipments/prepare` | OPERATOR_LOGISTIC |
| `PUT` | `/api/shipments/{id}/dispatch` | OPERATOR_LOGISTIC |
| `PUT` | `/api/shipments/{id}/confirm-delivery` | OPERATOR_LOGISTIC |
| `GET` | `/api/shipments/{id}` | OPERATOR_LOGISTIC, SUPERVISOR, buyer owner |
