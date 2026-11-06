# Catalog Management Services Specification

## 1. Business Purpose
Domain 5: product catalog. Products are physical (require inventory and dispatch) or digital (immediate delivery after payment).

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`RegisterProductService`](../../../src/main/java/application/domain/services/RegisterProductService.java) | Registers a product with unique ID and SKU. |
| [`GetProductService`](../../../src/main/java/application/domain/services/GetProductService.java) | Queries a product. |
| [`GetPublishedCatalogService`](../../../src/main/java/application/domain/services/GetPublishedCatalogService.java) | Public catalog: PUBLISHED products only. |
| [`PublishProductService`](../../../src/main/java/application/domain/services/PublishProductService.java) | → `PUBLISHED`. |
| [`SuspendProductService`](../../../src/main/java/application/domain/services/SuspendProductService.java) | `PUBLISHED` → `SUSPENDED`. |
| [`DiscontinueProductService`](../../../src/main/java/application/domain/services/DiscontinueProductService.java) | → `DISCONTINUED` (final). |
| [`UpdateProductPriceService`](../../../src/main/java/application/domain/services/UpdateProductPriceService.java) | Updates the price (> 0). |
| [`AddProductVariantService`](../../../src/main/java/application/domain/services/AddProductVariantService.java) | Adds a variant (color, size, model...). |

## 3. Business Rules Enforced

1. Product type: `PHYSICAL` or `DIGITAL`. Status: exactly `PUBLISHED`, `SUSPENDED`, `DISCONTINUED` (selection made by the seller on registration).
2. Only PUBLISHED products are visible in the public catalog and can be added to a cart (section 6.1, step 4).
3. Only the owning seller registers and administers its products (matrix: "Product registration: Seller").

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/products` | SELLER |
| `GET` | `/api/products` | any authenticated user (public catalog) |
| `GET` | `/api/products/{id}` | any (non-published: owner seller / SUPERVISOR) |
| `PUT` | `/api/products/{id}/publish · /suspend · /discontinue · /price` | owner SELLER |
| `POST` | `/api/products/{id}/variants` | owner SELLER |
