# User Authentication & Identification Domain Service Specification

## 1. Context & Business Purpose
The **`UserAuthenticationService`** establishes the identity foundation of NexusMarket (OBJ-01 & Dominio 1). It guarantees correct user identification, secure access control, credential verification, and status validation across all platform interactions.

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`UserAuthenticationService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/UserAuthenticationService.java)
- **Dependencies**: [`UserRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/UserRepositoryPort.java)
- **Associated Models**: [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/User.java), [`Email`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Email.java), [`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserRole.java), [`UserStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserStatus.java)

---

## 3. Core Business Invariants & Rules

1. **Unique Identity & Email Invariant (RG-11)**: No two users may share the same system `identifier` or `email` address.
2. **Single Role Constraint (RG-02)**: Each user account is assigned exactly one role (`BUYER`, `SELLER`, `OPERATOR_LOGISTIC`, `ADMIN`, `SUPERVISOR`).
3. **Active Account Requirement**: Authentication requires user status to be `ACTIVE`. Accounts marked as `BLOCKED` or `PENDING_INCORPORATION` are rejected with `InvalidDomainStateException`.

---

## 4. Method Signatures & Logic Flow

```java
public User authenticate(Email email);
public User registerUser(User user);
public void blockUser(String identifier);
public void activateUser(String identifier);
```
