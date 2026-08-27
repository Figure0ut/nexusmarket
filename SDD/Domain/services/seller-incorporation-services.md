# Seller Incorporation Domain Service Specification

## 1. Context & Business Purpose
The **Seller Incorporation Domain Service** governs merchant onboarding and validation into NexusMarket (OBJ-02 & Dominio 3). 

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.seller`
- **Associated Aggregate Roots**: [`Seller`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/Seller.java), [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/User.java)
- **Associated Value Objects**: [`TaxIdentifier`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/TaxIdentifier.java), [`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/UserRole.java), [`UserStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/UserStatus.java)

---

## 3. Core Business Invariants & Rules

1. **Admin-Only Incorporation Rule**: Merchants cannot self-register. Incorporation MUST be executed by a user holding the `ADMIN` role.
2. **Corporate Tax Validation**: The seller must provide a valid corporate `TaxIdentifier` and `corporateName`.
3. **Status Lifecycle Transition**:
   - Initial state: `PENDING_INCORPORATION`
   - State upon `incorporate(adminUser)` execution: `ACTIVE`
4. **Already Incorporated Protection**: Re-incorporating an already `ACTIVE` seller throws `IllegalStateException`.

---

## 4. Method Signatures & Logic Flow

```java
public void incorporate(User adminUser) {
    if (adminUser == null || adminUser.getRole() != UserRole.ADMIN) {
        throw new IllegalStateException("Incorporation Failure: ADMIN role required.");
    }
    if (getStatus() == UserStatus.ACTIVE) {
        throw new IllegalStateException("Seller is already incorporated.");
    }
    changeStatus(UserStatus.ACTIVE);
}
```
