# Seller Incorporation Service Specification

## 1. Business Purpose
Domain 3: sellers cannot self-register; the Administrator registers the seller **and its first warehouse** (section 6.1, step 1).

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`IncorporateSellerService`](../../../src/main/java/application/domain/services/IncorporateSellerService.java) | Validates the admin, registers the seller (`PENDING_INCORPORATION` → `ACTIVE` via `Seller.incorporate`) and its first `SELLER` warehouse. |
| [`GetProductsBySellerService`](../../../src/main/java/application/domain/services/GetProductsBySellerService.java) | Lists the products of a seller. |

## 3. Business Rules Enforced

1. Only an ACTIVE `ADMIN` can incorporate sellers.
2. The first warehouse is mandatory, of type `SELLER` and owned by the new seller.
3. Identifier, identity document and email uniqueness also apply to sellers.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/sellers` | ADMIN |
| `GET` | `/api/sellers/{sellerId}/products` | the seller itself, SUPERVISOR |
