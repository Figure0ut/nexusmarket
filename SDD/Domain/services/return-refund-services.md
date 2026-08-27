# Return & Refund Domain Service Specification

## 1. Context & Business Purpose
The **Return & Refund Domain Service** manages customer post-sale product return requests, physical item inspection, and monetary refunds (OBJ-11 & Dominio 11).

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.returns`
- **Associated Aggregate Roots**: [`ReturnRequest`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/ReturnRequest.java), [`Refund`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/Refund.java)
- **Associated Value Objects**: [`ReturnReason`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/ReturnReason.java), [`ReturnStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/ReturnStatus.java), [`RefundStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/RefundStatus.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java)

---

## 3. Core Business Invariants & Rules

1. **Return Reason Requirement**: Return requests must specify a valid reason (`DAMAGED`, `WRONG_ITEM`, `DEFECTIVE`, `CUSTOMER_REGRET`).
2. **Inspection & Inventory Action**:
   - Resellable items: Added back to `availableStock`.
   - Damaged items: Transferred to `damagedStock`.
3. **Refund Constraint**: Refund amount cannot exceed original item purchase price.
