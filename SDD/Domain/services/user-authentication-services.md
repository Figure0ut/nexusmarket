# User & Authentication Atomic Domain Services Specification

## 1. Context & Business Purpose
The User & Authentication domain governs participant identity, registration, status management (`ACTIVE`, `BLOCKED`, `PENDING_INCORPORATION`), and uniqueness validation (OBJ-01, OBJ-03 & Dominio 1). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`UserRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/UserRepositoryPort.java)
- **Associated Models**: [`User`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/User.java), [`Email`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Email.java)

### Services
1. **`AuthenticateUserService`**: Validates user credentials/email, rejecting blocked or unverified users.
2. **`RegisterUserService`**: Validates email and identifier uniqueness before saving a new user.
3. **`BlockUserService`**: Transitions user status to `BLOCKED`.
4. **`ActivateUserService`**: Transitions user status to `ACTIVE`.
5. **`GetUserService`**: Retrieves a user by identifier.

---

## 3. Core Business Invariants & Rules

1. **Email & Identifier Uniqueness**: No two users may share the same email address or identifier.
2. **Active State Authentication**: Authentication fails if the user status is `BLOCKED` or `PENDING_INCORPORATION`.
3. **Single Role Constraint (RG-02)**: Each user account is assigned exactly one role (`BUYER`, `SELLER`, `OPERATOR_LOGISTIC`, `ADMIN`, `SUPERVISOR`).

---

## 4. Method Signatures

```java
// AuthenticateUserService
public User authenticate(Email email);

// RegisterUserService
public User registerUser(User user);

// BlockUserService
public void blockUser(String identifier);

// ActivateUserService
public void activateUser(String identifier);

// GetUserService
public User getUser(String identifier);
```
