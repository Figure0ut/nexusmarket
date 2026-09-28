# Authorization Domain Service Specification

## 1. Context & Business Purpose
The **`AuthorizationService`** enforces platform-wide quality controls, operational role constraints, and the formal **Responsibility Matrix** (Restricciones Generales RG-01, RG-02, RG-03, and Sección 12).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`AuthorizationService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/AuthorizationService.java)
- **Associated Models**: [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/User.java), [`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserRole.java), [`UserStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserStatus.java)

---

## 3. Core Business Invariants & Rules

1. **RG-01 (Mandatory Active Authentication)**: Every operation must be executed by an authenticated, `ACTIVE` user. Blocked or unverified users are rejected.
2. **RG-02 (Single Role Assignment)**: Each participant possesses exactly one role within the system (`BUYER`, `SELLER`, `OPERATOR_LOGISTIC`, `ADMIN`, `SUPERVISOR`).
3. **RG-03 (Role Segregation)**: No participant may execute operations or manage data outside their assigned role boundaries.
4. **Responsibility Matrix (Section 12)**:
   - `SELLER_REGISTRATION`: `ADMIN` only.
   - `PRODUCT_REGISTRATION`: `SELLER` only.
   - `INVENTORY_MANAGEMENT`: `SELLER`, `OPERATOR_LOGISTIC`.
   - `ORDER_MANAGEMENT`: `BUYER`, `SELLER`, `OPERATOR_LOGISTIC`.
   - `REFUND_MANAGEMENT`: `BUYER`, `ADMIN`.

---

## 4. Method Signatures

```java
public void validateProcessAuthorization(User actor, BusinessProcess process);
public boolean isAuthorized(User actor, BusinessProcess process);
```
