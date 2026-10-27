# Order & Checkout Atomic Domain Services Specification

## 1. Context & Business Purpose
The Order & Checkout domain orchestrates shopping cart conversion into formal orders, payment processing, cancellation, and order querying (OBJ-07, OBJ-08 & Dominio 7). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java), [`PaymentGatewayPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/PaymentGatewayPort.java), [`NotificationPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/NotificationPort.java)
- **Associated Models**: [`Cart`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Cart.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Order.java)

### Services
1. **`CheckoutCartService`**: Converts active shopping cart items into a formal `Order` starting in `PENDING_PAYMENT` status and clears the cart.
2. **`ProcessOrderPaymentService`**: Calls the payment gateway, marks the order `PAID`, and dispatches an email notification.
3. **`CancelOrderService`**: Transitions a non-finalized order to `CANCELLED`.
4. **`GetOrderService`**: Retrieves an order by its unique identifier.
5. **`GetOrdersByBuyerService`**: Retrieves all orders placed by a specific buyer.

---

## 3. Core Business Invariants & Rules

1. **Empty Cart Prohibition**: Checkout cannot proceed if the buyer's cart is empty.
2. **Order Lifecycle Transitions**:
   - `PENDING_PAYMENT` -> `PAID` -> `DISPATCHED` -> `DELIVERED_FINALIZED`
3. **Immutability of Finalized Orders**: Once delivered and finalized, orders cannot be cancelled or modified.

---

## 4. Method Signatures

```java
// CheckoutCartService
public Order checkoutCart(String buyerId, String orderId, Address shippingAddress);

// ProcessOrderPaymentService
public boolean processOrderPayment(String orderId, String paymentToken, Email buyerEmail);

// CancelOrderService
public void cancelOrder(String orderId);

// GetOrderService
public Order getOrder(String orderId);

// GetOrdersByBuyerService
public List<Order> getOrdersByBuyer(String buyerId);
```
