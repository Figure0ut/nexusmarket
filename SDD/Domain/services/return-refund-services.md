# Returns & Refunds Services Specification

## 1. Business Purpose
OBJ-11: returns and refunds. Matrix "Refund management": Buyer and Administrator.

## 2. Atomic Services (`application.domain.services`)

| Service | Responsibility |
| :--- | :--- |
| [`RequestReturnService`](../../../src/main/java/application/domain/services/RequestReturnService.java) | The buyer requests the return of a product of its DELIVERED_FINALIZED order (`REQUESTED`). |
| [`ApproveReturnService`](../../../src/main/java/application/domain/services/ApproveReturnService.java) | `REQUESTED` → `APPROVED`. |
| [`RejectReturnService`](../../../src/main/java/application/domain/services/RejectReturnService.java) | `REQUESTED` → `REJECTED`. |
| [`ProcessReturnAndRefundService`](../../../src/main/java/application/domain/services/ProcessReturnAndRefundService.java) | `APPROVED` → `ITEM_RECEIVED`; RETURN movement in the original warehouse (reusable → available, otherwise damaged); refunds the amount paid for that line. |
| [`GetReturnRequestService`](../../../src/main/java/application/domain/services/GetReturnRequestService.java) | Queries a return. |

## 3. Business Rules Enforced

1. The finalized order is never modified; the return is an independent aggregate.
2. The product must belong to the order and the order to the buyer.
3. The refunded amount is calculated, never typed by the client.
4. Only one active return per order line.

## 4. REST Endpoints

| Method | Path | Allowed roles |
| :--- | :--- | :--- |
| `POST` | `/api/returns/request` | the buyer owner |
| `PUT` | `/api/returns/{id}/approve · /reject` | ADMIN |
| `POST` | `/api/returns/process-refund` | ADMIN |
| `GET` | `/api/returns/{id}` | buyer owner, ADMIN, SUPERVISOR |
