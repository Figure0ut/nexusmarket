# Shopping Cart Management Domain Service Specification

## 1. Context & Business Purpose
The **`CartManagementService`** coordinates shopping cart operations prior to checkout. It ensures buyers have an active cart, allows adding items with quantity accumulation, supports removing items, and enables clearing cart contents.

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`CartManagementService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/CartManagementService.java)
- **Dependencies**: [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java)
- **Associated Models**: [`Cart`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Cart.java), [`CartItem`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/CartItem.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Money.java)

---

## 3. Core Business Invariants & Rules

1. **Auto-provisioning**: If a cart does not exist for a given `buyerId`, `getOrCreateCart` instantiates and persists a new empty `Cart`.
2. **Positive Quantity**: Adding items requires $quantity > 0$.
3. **Quantity Accumulation**: Adding an item that already exists in the cart increments the line item's existing quantity.
4. **Item Removal**: Removing an item safely extracts it from the cart collection.
5. **Persistence Synchronization**: All mutations persist immediately via `OrderRepositoryPort.saveCart(cart)`.

---

## 4. Method Signatures

```java
public Cart getOrCreateCart(String buyerId);
public Cart addItemToCart(String buyerId, String productId, Money unitPrice, int quantity);
public Cart removeItemFromCart(String buyerId, String productId);
public void clearCart(String buyerId);
```
