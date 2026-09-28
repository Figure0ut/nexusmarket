# Return & Refund Domain Service Specification

## 1. Context & Business Purpose
The **`ReturnRefundService`** manages customer post-sale product return requests, physical item inspection, and monetary refunds (OBJ-11 & Dominio 11).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`ReturnRefundService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/ReturnRefundService.java)
- **Dependencies**: [`ReturnRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/ReturnRepositoryPort.java), [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java), [`InventoryRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/InventoryRepositoryPort.java), [`PaymentGatewayPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/PaymentGatewayPort.java)
- **Associated Models**: [`ReturnRequest`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/ReturnRequest.java), [`Refund`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Refund.java)

---

## 3. Core Business Invariants & Rules

1. **Delivered Order Prerequisite**: Return requests can only be initiated for orders in `DELIVERED_FINALIZED` status.
2. **Inspection & Inventory Action**:
   - Reusable items: increment available stock.
   - Damaged items: increment damaged stock.
3. **Refund Processing**: Monetary refunds are processed via the payment gateway port upon item receipt.

---

## 4. Method Signatures

```java
public ReturnRequest requestReturn(String returnId, String orderId, String buyerId, String productId, ReturnReason reason);
public ReturnRequest getReturnRequest(String returnId);
public void approveReturn(String returnId);
public void rejectReturn(String returnId);
public Refund processReturnedItemAndRefund(String refundId, String returnId, String warehouseId, boolean isReusable, Money refundAmount);
```
