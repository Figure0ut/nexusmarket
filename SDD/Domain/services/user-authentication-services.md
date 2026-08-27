# User Authentication & Identification Domain Service Specification

## 1. Context & Business Purpose
The **User Authentication Domain Service** establishes the identity foundation of NexusMarket (OBJ-01 & Dominio 1). It guarantees correct user identification, secure access control, credential verification, and single-role assignment across all platform interactions.

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.user`
- **Associated Aggregate Roots**: [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/User.java), [`Buyer`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/Buyer.java), [`Seller`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/Seller.java)
- **Associated Value Objects**: [`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/UserRole.java), [`UserStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/UserStatus.java), [`Email`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Email.java)

---

## 3. Core Business Invariants & Rules

1. **Unique Identity & Email Invariant (RG-11)**: No two users may share the same system `identifier` or `email` address.
2. **Single Role Constraint (RG-02)**: Each user account is assigned exactly one role (`BUYER`, `SELLER`, `OPERATOR_LOGISTIC`, `ADMIN`, `SUPERVISOR`).
3. **Role Authorization Scope (RG-03)**: Users are restricted from executing operations outside their defined role authority.
4. **Active Account Requirement**: Operations require user status to be `ACTIVE`. Accounts marked as `BLOCKED` or `PENDING_INCORPORATION` are rejected.

---

## 4. Key Constructor Validation & Logic Flow

```
[ User Auth Request ] ──> Validate Email & ID Format ──> Check Account Status (ACTIVE) ──> Verify Role Authority
```

- Constructor & Factory Method Invariants:
  - Validates `Email` using RFC rules upon instantiation.
  - Ensures `fullName` is non-empty.
  - Throws `IllegalArgumentException` on invalid user data.
  - Throws `IllegalStateException` on unauthorized role operations.
