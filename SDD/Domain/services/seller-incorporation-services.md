# Seller Incorporation Domain Service Specification

## 1. Context & Business Purpose
The **`SellerIncorporationService`** governs merchant onboarding and validation into NexusMarket (OBJ-02 & Dominio 3).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`SellerIncorporationService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/SellerIncorporationService.java)
- **Dependencies**: [`UserRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/UserRepositoryPort.java)
- **Associated Models**: [`Seller`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Seller.java), [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/User.java)

---

## 3. Core Business Invariants & Rules

1. **Admin-Only Incorporation Rule**: Merchants cannot self-register. Incorporation MUST be executed by a user holding the `ADMIN` role.
2. **Corporate Tax Validation**: The seller must provide a valid corporate `TaxIdentifier` and `corporateName`.
3. **Status Lifecycle Transition**:
   - Initial state: `PENDING_INCORPORATION`
   - State upon successful incorporation: `ACTIVE`

---

## 4. Method Signatures

```java
public void incorporateSeller(String sellerId, User adminUser);
public void incorporateSeller(Seller seller, User adminUser);
```
