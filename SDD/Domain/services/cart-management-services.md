# Shopping Cart Atomic Domain Services Specification

## 1. Context & Business Purpose
The Shopping Cart domain coordinates shopping cart operations prior to checkout. Each operation is implemented as an atomic domain service ensuring high cohesion and single responsibility.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java)
- **Associated Models**: [`Cart`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Cart.java), [`CartItem`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/CartItem.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Money.java)

### Services
1. **`GetOrCreateCartService`**: Retrieves an existing cart or auto-provisions a new empty cart for a buyer.
2. **`AddItemToCartService`**: Adds a product item with quantity to a buyer's cart, incrementing existing item quantities.
3. **`RemoveItemFromCartService`**: Removes a specific product from a buyer's cart.
4. **`ClearCartService`**: Clears all items from a buyer's cart.

---

## 3. Core Business Invariants & Rules

1. **Auto-provisioning**: If a cart does not exist for a given `buyerId`, `GetOrCreateCartService` instantiates and persists a new empty `Cart`.
2. **Positive Quantity**: Adding items requires $quantity > 0$.
3. **Quantity Accumulation**: Adding an item that already exists in the cart increments the line item's existing quantity.
4. **Item Removal**: Removing an item safely extracts it from the cart collection.
5. **Persistence Synchronization**: All mutations persist immediately via `OrderRepositoryPort.saveCart(cart)`.

---

## 4. Method Signatures

```java
// GetOrCreateCartService
public Cart getOrCreateCart(String buyerId);

// AddItemToCartService
public Cart addItemToCart(String buyerId, String productId, Money unitPrice, int quantity);

// RemoveItemFromCartService
public Cart removeItemFromCart(String buyerId, String productId);

// ClearCartService
public void clearCart(String buyerId);
```
