# Order Checkout Domain Service Specification

## 1. Context & Business Purpose
The **Order Checkout Domain Service** orchestrates shopping cart conversion into formal orders, total computation, and order state machine transitions (OBJ-07, OBJ-08 & Dominio 7).

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.order`
- **Associated Aggregate Roots**: [`Cart`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/order/Cart.java), [`Order`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/order/Order.java)
- **Associated Value Objects**: [`OrderStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/order/OrderStatus.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java), [`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Address.java)

---

## 3. Order Lifecycle State Machine

```
[ CART ] ──(Submit)──> [ PENDING_PAYMENT ] ──(Payment Validated)──> [ PAID ] ──(Warehouse Dispatch)──> [ DISPATCHED ] ──(Delivery Confirmed)──> [ DELIVERED_FINALIZED ]
                                 │
                            (Cancel Order)
                                 ▼
                           [ CANCELLED ]
```

---

## 4. Core Business Invariants & Rules

1. **Finalized Order Immutability (Validaciones Críticas 11)**: A finalized or delivered order (`DELIVERED_FINALIZED`) **cannot be modified or cancelled** under any circumstance.
2. **Total Amount Calculation**: Total order amount is computed automatically as the sum of all line item subtotals (`unitPrice * quantity`).
3. **Cart Item Rules**: Cart items require positive quantity ($quantity > 0$) and valid unit price.
