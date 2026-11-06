# Order Services Specification

## 1. Business Purpose
Domain 7: the order is the formal commercial commitment. Lifecycle: `CART` → `PENDING_PAYMENT` → `PAID` → `DISPATCHED` → `DELIVERED_FINALIZED`.

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`CheckoutCartService`](../../../src/main/java/application/domain/services/CheckoutCartService.java) | CART → PENDING_PAYMENT. Validates every item and RESERVES the stock of physical items (all-or-nothing). |
| [`ProcessOrderPaymentService`](../../../src/main/java/application/domain/services/ProcessOrderPaymentService.java) | Validates the payment through `PaymentGatewayPort`: → PAID (digital-only orders → DELIVERED_FINALIZED), issues the invoice and notifies the buyer. |
| [`GetOrderService`](../../../src/main/java/application/domain/services/GetOrderService.java) | Queries an order. |
| [`GetOrdersByBuyerService`](../../../src/main/java/application/domain/services/GetOrdersByBuyerService.java) | Orders of a buyer. |
| [`GetAllOrdersService`](../../../src/main/java/application/domain/services/GetAllOrdersService.java) | All orders. |

## 3. Business Rules Enforced

1. Every physical item records the warehouse where its stock was reserved.
2. Digital products are delivered immediately after payment.
3. A finalized order can never be modified (`Order.ensureNotFinalized`).
4. The specification defines no cancellation, therefore no cancellation state exists.
5. Visibility: buyers see their own orders, sellers the orders that contain their products, operators and supervisors all orders.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/orders/checkout/{buyerId}` | the buyer itself |
| `POST` | `/api/orders/{orderId}/pay` | the buyer owner |
| `GET` | `/api/orders` | BUYER, SELLER, OPERATOR_LOGISTIC, SUPERVISOR (filtered) |
| `GET` | `/api/orders/{orderId}` | same (filtered) |
| `GET` | `/api/orders/buyer/{buyerId}` | same (filtered) |
