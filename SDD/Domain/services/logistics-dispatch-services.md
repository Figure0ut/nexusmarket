# Logistics & Dispatch Domain Service Specification

## 1. Context & Business Purpose
The **`LogisticsDispatchService`** coordinates physical packaging, carrier assignment, tracking number generation, and delivery confirmation (OBJ-10 & Dominio 10).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`LogisticsDispatchService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/LogisticsDispatchService.java)
- **Dependencies**: [`ShipmentRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/ShipmentRepositoryPort.java), [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java), [`WarehouseRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/WarehouseRepositoryPort.java)
- **Associated Models**: [`Shipment`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Shipment.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Order.java), [`Warehouse`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Warehouse.java)

---

## 3. Core Business Invariants & Rules

1. **Active Warehouse & Paid Order Validation**: Shipments can only be created from active warehouses for orders that are `PAID`.
2. **Carrier & Tracking Requirement**: Dispatching requires non-empty carrier and tracking identifiers.
3. **Coordinated Finalization**: Confirming shipment delivery finalizes the order (`DELIVERED_FINALIZED`).

---

## 4. Method Signatures

```java
public Shipment prepareShipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress);
public Shipment getShipment(String shipmentId);
public void dispatchShipment(String shipmentId, String carrier, String trackingNumber);
public void confirmDelivery(String shipmentId);
public void reportFailedDelivery(String shipmentId);
```
