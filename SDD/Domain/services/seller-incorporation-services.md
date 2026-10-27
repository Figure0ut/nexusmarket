# Seller Incorporation Atomic Domain Service Specification

## 1. Context & Business Purpose
The Seller Incorporation domain governs merchant onboarding (OBJ-02 & Dominio 2). Sellers cannot self-register; incorporation requires explicit validation and execution by an `ADMIN` user.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Class**: [`IncorporateSellerService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/IncorporateSellerService.java)
- **Dependencies**: [`UserRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/UserRepositoryPort.java)
- **Associated Models**: [`Seller`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Seller.java), [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/User.java)

---

## 3. Core Business Invariants & Rules

1. **Non-Self-Registration**: A seller account starts in `PENDING_INCORPORATION` and cannot activate itself.
2. **Admin Authority**: Only an actor with `UserRole.ADMIN` is authorized to execute incorporation.
3. **Status Transition**: On incorporation, status transitions to `ACTIVE`.

---

## 4. Method Signatures

```java
// IncorporateSellerService
public void incorporateSeller(String sellerId, User adminUser);
public void incorporateSeller(Seller seller, User adminUser);
```
