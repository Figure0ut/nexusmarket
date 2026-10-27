# Returns & Refunds Atomic Domain Services Specification

## 1. Context & Business Purpose
The Returns & Refunds domain coordinates post-sale reverse logistics, return authorization, item inspection (restock reusable items vs. mark damaged), and refund processing (OBJ-10 & Dominio 10). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`ReturnRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/ReturnRepositoryPort.java), [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java), [`InventoryRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/InventoryRepositoryPort.java), [`PaymentGatewayPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/PaymentGatewayPort.java)
- **Associated Models**: [`ReturnRequest`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/ReturnRequest.java), [`Refund`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Refund.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Order.java), [`Inventory`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Inventory.java)

### Services
1. **`RequestReturnService`**: Initiates a return request on a delivered and finalized order.
2. **`GetReturnRequestService`**: Retrieves a return request by its identifier.
3. **`ApproveReturnService`**: Approves a requested return.
4. **`RejectReturnService`**: Rejects a requested return.
5. **`ProcessReturnAndRefundService`**: Receives returned item, restocks or marks damaged, and executes financial refund via payment gateway.

---

## 3. Core Business Invariants & Rules

1. **Delivered Status Prerequisite**: Returns can only be requested for orders in `DELIVERED_FINALIZED` status.
2. **Approval Requirement**: Items can only be received and refunded after return request approval.
3. **Inventory Condition Sync**: Reusable items increment available stock (`addStock`), while defective items increment damaged stock (`markAsDamaged`).

---

## 4. Method Signatures

```java
// RequestReturnService
public ReturnRequest requestReturn(String returnId, String orderId, String buyerId, String productId, ReturnReason reason);

// GetReturnRequestService
public ReturnRequest getReturnRequest(String returnId);

// ApproveReturnService
public void approveReturn(String returnId);

// RejectReturnService
public void rejectReturn(String returnId);

// ProcessReturnAndRefundService
public Refund processReturnedItemAndRefund(String refundId, String returnId, String warehouseId, boolean isReusable, Money refundAmount);
```
