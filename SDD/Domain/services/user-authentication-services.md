# User & Buyer Management Services Specification

## 1. Business Purpose
Domain 1 (User Administration) and Domain 2 (Buyer Management): registration with uniqueness checks, authentication by email, blocking/activation and buyer addresses.

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`RegisterUserService`](../../../src/main/java/application/domain/services/RegisterUserService.java) | Registers a user validating unique identifier, identity document and email. |
| [`AuthenticateUserService`](../../../src/main/java/application/domain/services/AuthenticateUserService.java) | Authenticates a user by email; only `ACTIVE` users pass. |
| [`BlockUserService`](../../../src/main/java/application/domain/services/BlockUserService.java) | Sets status `BLOCKED`. |
| [`ActivateUserService`](../../../src/main/java/application/domain/services/ActivateUserService.java) | Sets status `ACTIVE`. |
| [`GetUserService`](../../../src/main/java/application/domain/services/GetUserService.java) | Queries a user. |
| [`GetAllUsersService`](../../../src/main/java/application/domain/services/GetAllUsersService.java) | Lists all users. |
| [`UpdateBuyerMainAddressService`](../../../src/main/java/application/domain/services/UpdateBuyerMainAddressService.java) | Updates the main delivery address of a buyer. |
| [`AddBuyerAdditionalAddressService`](../../../src/main/java/application/domain/services/AddBuyerAdditionalAddressService.java) | Adds a secondary delivery address to a buyer. |

## 3. Business Rules Enforced

1. User attributes: identifier (unique), identity document (unique, section 11), full name (not empty), email (unique, main access channel), role (one per user), status (`ACTIVE`, `BLOCKED`, `PENDING_INCORPORATION`).
2. Buyer attributes: main address (mandatory), additional addresses (optional), commercial status (mandatory).
3. Buyers self-register (public endpoint). Internal users (ADMIN, OPERATOR_LOGISTIC, SUPERVISOR) are registered by the Administrator. Sellers are never created through these endpoints.
4. A buyer never manages information of other buyers.
5. An initial Administrator (`admin@nexusmarket.com`) is created at startup by `InitialAdministratorConfig`.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | public |
| `POST` | `/api/users/register` | public (buyer self-registration) |
| `POST` | `/api/users` | ADMIN |
| `GET` | `/api/users/me` | any authenticated user |
| `GET` | `/api/users` | ADMIN, SUPERVISOR |
| `GET` | `/api/users/{id}` | self, ADMIN, SUPERVISOR |
| `PUT` | `/api/users/{id}/block` | ADMIN |
| `PUT` | `/api/users/{id}/activate` | ADMIN |
| `PUT` | `/api/users/{id}/main-address` | the buyer itself |
| `POST` | `/api/users/{id}/additional-addresses` | the buyer itself |
