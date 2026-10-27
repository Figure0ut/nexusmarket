# Logistics & Dispatch Atomic Domain Services Specification

## 1. Context & Business Purpose
The Logistics & Dispatch domain manages warehouse package preparation, carrier dispatch with tracking numbers, delivery confirmation, and failed delivery handling (OBJ-08 & Dominio 8). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`ShipmentRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/ShipmentRepositoryPort.java), [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java), [`WarehouseRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java)
- **Associated Models**: [`Shipment`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Shipment.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Order.java), [`Warehouse`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Warehouse.java)

### Services
1. **`PrepareShipmentService`**: Creates a shipment record for paid orders dispatched from active warehouses.
2. **`GetShipmentService`**: Retrieves a shipment by its identifier.
3. **`DispatchShipmentService`**: Assigns carrier, tracking code, and marks order `DISPATCHED`.
4. **`ConfirmDeliveryService`**: Finalizes shipment delivery and transitions order to `DELIVERED_FINALIZED`.
5. **`ReportFailedDeliveryService`**: Records failed delivery status on a shipment.

---

## 3. Core Business Invariants & Rules

1. **Paid Order Prerequisite**: Shipments can only be prepared for orders in `PAID` status.
2. **Active Warehouse Invariant**: Shipments must originate from active storage facilities.
3. **Shipment Lifecycle**: `PREPARING` -> `IN_TRANSIT` -> `DELIVERED` or `FAILED`.

---

## 4. Method Signatures

```java
// PrepareShipmentService
public Shipment prepareShipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress);

// GetShipmentService
public Shipment getShipment(String shipmentId);

// DispatchShipmentService
public void dispatchShipment(String shipmentId, String carrier, String trackingNumber);

// ConfirmDeliveryService
public void confirmDelivery(String shipmentId);

// ReportFailedDeliveryService
public void reportFailedDelivery(String shipmentId);
```
