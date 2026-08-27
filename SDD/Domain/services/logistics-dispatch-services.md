# Logistics & Dispatch Domain Service Specification

## 1. Context & Business Purpose
The **Logistics & Dispatch Domain Service** coordinates physical packaging, carrier assignment, tracking number generation, and delivery tracking (OBJ-10 & Dominio 10).

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.logistics`
- **Associated Aggregate Roots**: [`Shipment`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/logistics/Shipment.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/order/Order.java)
- **Associated Value Objects**: [`ShipmentStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/logistics/ShipmentStatus.java), [`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Address.java)

---

## 3. Core Business Invariants & Rules

1. **Carrier & Tracking Requirement**: Dispatching a shipment requires a non-empty `carrier` name and non-empty `trackingNumber`.
2. **Status Transition**: `PREPARING` -> `IN_TRANSIT` -> `DELIVERED` / `FAILED`.
3. **Delivery Confirmation**: Delivery confirmation updates the shipment to `DELIVERED` and triggers order finalization.
