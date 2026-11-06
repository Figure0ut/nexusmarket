# Billing Services Specification

## 1. Business Purpose
OBJ-09: billing of purchases. The invoice is issued automatically when the payment of the order is validated (section 6.1, step 6).

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`CreateInvoiceService`](../../../src/main/java/application/domain/services/CreateInvoiceService.java) | Issues one invoice (`INVOICE-{orderId}`) per paid order with the order total; the buyer identity document is the tax identifier. |
| [`GetInvoiceService`](../../../src/main/java/application/domain/services/GetInvoiceService.java) | Queries by invoice ID. |
| [`GetInvoiceByOrderService`](../../../src/main/java/application/domain/services/GetInvoiceByOrderService.java) | Queries by order ID. |

## 3. Business Rules Enforced

1. An invoice cannot be issued before the payment is validated.
2. Only one invoice per order.
3. The specification defines no invoice lifecycle: the only status is `ISSUED`.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `GET` | `/api/invoices/{invoiceId}` | buyer owner, ADMIN, SUPERVISOR |
| `GET` | `/api/invoices/order/{orderId}` | buyer owner, ADMIN, SUPERVISOR |
