# Cart Management Services Specification

## 1. Business Purpose
OBJ-07: the cart is the first stage of the order lifecycle (`CART`: provisional selection of products).

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`GetOrCreateCartService`](../../../src/main/java/application/domain/services/GetOrCreateCartService.java) | Gets or creates the cart of a buyer. |
| [`AddItemToCartService`](../../../src/main/java/application/domain/services/AddItemToCartService.java) | Adds a PUBLISHED product; the price is taken from the catalog. |
| [`RemoveItemFromCartService`](../../../src/main/java/application/domain/services/RemoveItemFromCartService.java) | Removes an item. |
| [`ClearCartService`](../../../src/main/java/application/domain/services/ClearCartService.java) | Empties the cart. |

## 3. Business Rules Enforced

1. Only the buyer owner of the cart can use it.
2. The client never sends prices.
3. Quantity must be greater than zero.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `GET` | `/api/carts/{buyerId}` | the buyer itself |
| `POST` | `/api/carts/{buyerId}/items` | the buyer itself |
| `DELETE` | `/api/carts/{buyerId}/items/{productId}` | the buyer itself |
| `DELETE` | `/api/carts/{buyerId}` | the buyer itself |
