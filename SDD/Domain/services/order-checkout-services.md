# Order Checkout Domain Service Specification

## 1. Context & Business Purpose
The **`OrderCheckoutService`** orchestrates shopping cart conversion into formal orders, total computation, and order state machine transitions (OBJ-07, OBJ-08 & Dominio 7).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`OrderCheckoutService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/OrderCheckoutService.java)
- **Dependencies**: [`OrderRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/OrderRepositoryPort.java), [`PaymentGatewayPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/PaymentGatewayPort.java), [`NotificationPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/NotificationPort.java)
- **Associated Models**: [`Cart`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Cart.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Order.java)

---

## 3. Core Business Invariants & Rules

1. **Empty Cart Prohibition**: Checkout cannot proceed if the buyer's cart is empty.
2. **Order Lifecycle Transitions**:
   - `CART` -> `PENDING_PAYMENT` -> `PAID` -> `DISPATCHED` -> `DELIVERED_FINALIZED`
3. **Immutability of Finalized Orders**: Once delivered and finalized, orders cannot be cancelled or modified.

---

## 4. Method Signatures

```java
public Order checkoutCart(String buyerId, String orderId, Address shippingAddress);
public boolean processOrderPayment(String orderId, String paymentToken, Email buyerEmail);
public void cancelOrder(String orderId);
```
