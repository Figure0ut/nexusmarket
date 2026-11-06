# Authorization Service Specification

## 1. Business Purpose
`ValidateAuthorizationService` enforces the general restrictions RG-01, RG-02, RG-03 and the Responsibility Matrix of section 12 of the functional specification.

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`ValidateAuthorizationService`](../../../src/main/java/application/domain/services/ValidateAuthorizationService.java) | Validates execution rights (`validateProcessAuthorization`), consultation rights (`validateQueryAuthorization`) and ownership (`validateOwnership`). |

## 3. Business Rules Enforced

1. **RG-01**: every operation requires an authenticated, `ACTIVE` actor. The REST adapter resolves the actor from the `X-User-Email` header (`AuthenticatedUserResolver`).
2. **RG-02**: each user has exactly one role (`UserRole`). `Buyer` and `Seller` subclasses refuse any other role.
3. **RG-03**: no participant manages information outside its role. Buyers and sellers only manage their own data (`validateOwnership`).
4. **Responsibility Matrix (exact copy of section 12)**: `SELLER_REGISTRATION` = ADMIN; `PRODUCT_REGISTRATION` = SELLER; `INVENTORY_MANAGEMENT` = SELLER, OPERATOR_LOGISTIC; `ORDER_MANAGEMENT` = BUYER, SELLER, OPERATOR_LOGISTIC; `REFUND_MANAGEMENT` = BUYER, ADMIN.
5. **Processes not listed in the matrix**, assigned from the participant descriptions of section 5: `WAREHOUSE_MANAGEMENT` = ADMIN ("responsible for the administration of sellers and warehouses"); `USER_ADMINISTRATION` = ADMIN; `CART_MANAGEMENT` = BUYER; `SHIPMENT_MANAGEMENT` = OPERATOR_LOGISTIC ("physical operation of warehouses and dispatches"); `ADMINISTRATIVE_REPORTS` = ADMIN, SUPERVISOR.
6. **Supervisor** ("consultation and operational follow-up profile"): may consult any process (`validateQueryAuthorization`) but never execute operations.
7. Authorization failures raise `AuthorizationException` → HTTP 403; missing/unknown/blocked user → HTTP 401.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `—` | `Used by every controller` | — |
