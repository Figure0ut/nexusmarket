# Inbound REST API Specification

## 1. Architectural Role
REST controllers (`application.infrastructure.adapters.in.rest.controllers`) are the **inbound (driving) adapters** of the Hexagonal Architecture. Each endpoint:

1. resolves the **authenticated user** (`AuthenticatedUserResolver`, RG-01),
2. checks **authorization** with the domain `ValidateAuthorizationService` (matrix of section 12, RG-03),
3. calls one or more **atomic domain services**,
4. records the operation in the **audit trail** (`AuditTrail` → MongoDB),
5. converts the result to a DTO.

## 2. Authentication (RG-01)
The specification leaves technical authentication mechanisms out of scope (section 3.2) and defines the email as the main access channel of a user (Domain 1). Therefore every request identifies its user with the header:

```
X-User-Email: admin@nexusmarket.com
```

The user must exist and be `ACTIVE`. Only two operations do not require it: `POST /api/auth/login` and `POST /api/users/register` (buyer self-registration, which creates the account itself).
An initial Administrator (`admin@nexusmarket.com`) is created at startup (configurable in `application.properties`).

## 3. Error Handling (`GlobalExceptionHandler`)

| Situation | HTTP status |
| :--- | :--- |
| Missing, unknown or non-active user (`AuthenticationRequiredException`) | `401 Unauthorized` |
| Operation outside the role / data of another user (`AuthorizationException`) | `403 Forbidden` |
| Entity not found (`EntityNotFoundException`) | `404 Not Found` |
| Duplicate identifier, identity document, email, SKU, ID (`DomainException`) | `409 Conflict` |
| Invalid state transition or business rule (`InvalidDomainStateException`, `IllegalStateException`, `IllegalArgumentException`) | `400 Bad Request` |
| Unexpected error | `500 Internal Server Error` |

```json
{ "timestamp": "2026-11-06T10:00:00", "status": 403, "error": "Forbidden",
  "message": "Authorization Failure (RG-03): User 'SUP-001' with role 'SUPERVISOR' is not authorized for process 'SELLER_REGISTRATION'.",
  "path": "/api/sellers" }
```

## 4. Endpoint Catalog

| Area | Method & path | Allowed roles |
| :--- | :--- | :--- |
| Auth | `POST /api/auth/login` | public |
| Users | `POST /api/users/register` | public (buyer self-registration) |
| | `POST /api/users` | ADMIN (internal users: ADMIN, OPERATOR_LOGISTIC, SUPERVISOR) |
| | `GET /api/users/me` | any |
| | `GET /api/users` · `GET /api/users/{id}` | ADMIN, SUPERVISOR (or self) |
| | `PUT /api/users/{id}/block` · `/activate` | ADMIN |
| | `PUT /api/users/{id}/main-address` · `POST /api/users/{id}/additional-addresses` | the buyer itself |
| Sellers | `POST /api/sellers` (seller + first warehouse) | ADMIN |
| | `GET /api/sellers/{id}/products` | the seller itself, SUPERVISOR |
| Warehouses | `POST /api/warehouses` · `PUT /{id}/activate` · `/deactivate` · `/location` | ADMIN |
| | `GET /api/warehouses` · `/{id}` · `/owner/{ownerId}` | ADMIN, SUPERVISOR, OPERATOR_LOGISTIC, SELLER |
| Catalog | `POST /api/products` | SELLER |
| | `GET /api/products` (PUBLISHED only) · `GET /api/products/{id}` | any |
| | `PUT /api/products/{id}/publish` · `/suspend` · `/discontinue` · `/price` · `POST /{id}/variants` | owner SELLER |
| Inventory | `POST /api/inventory/add` (INFLOW) · `POST /api/inventory/mark-damaged` (ADJUSTMENT) | SELLER (own products), OPERATOR_LOGISTIC |
| | `GET /api/inventory?productId=&warehouseId=` · `/product/{id}` · `/total-available/{id}` | SELLER (own), OPERATOR_LOGISTIC, SUPERVISOR |
| Cart | `GET /api/carts/{buyerId}` · `POST /{buyerId}/items` · `DELETE /{buyerId}/items/{productId}` · `DELETE /{buyerId}` | the buyer itself |
| Orders | `POST /api/orders/checkout/{buyerId}` (RESERVE) · `POST /api/orders/{id}/pay` (invoice) | the buyer itself |
| | `GET /api/orders` · `/{id}` · `/buyer/{buyerId}` | BUYER (own), SELLER (with its products), OPERATOR_LOGISTIC, SUPERVISOR |
| Invoices | `GET /api/invoices/{id}` · `/order/{orderId}` | buyer owner, ADMIN, SUPERVISOR |
| Shipments | `POST /api/shipments/prepare` · `PUT /{id}/dispatch` (OUTFLOW_SALE) · `PUT /{id}/confirm-delivery` | OPERATOR_LOGISTIC |
| | `GET /api/shipments/{id}` | OPERATOR_LOGISTIC, SUPERVISOR, buyer owner |
| Returns | `POST /api/returns/request` | buyer owner |
| | `PUT /api/returns/{id}/approve` · `/reject` · `POST /api/returns/process-refund` (RETURN) | ADMIN |
| | `GET /api/returns/{id}` | buyer owner, ADMIN, SUPERVISOR |
| Reports | `GET /api/reports/summary` | ADMIN, SUPERVISOR |
| Audit | `GET /api/audit` · `GET /api/audit/aggregate/{id}` | ADMIN, SUPERVISOR |

## 5. Testing the API
- **Postman**: import `postman/NexusMarket.postman_collection.json` and run the folders in order on a freshly started server.
- **Terminal**: `./mvnw spring-boot:run` and, in another terminal, `./test-api.sh` (42 steps of the business flow, including negative cases).
