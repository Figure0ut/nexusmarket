# Inbound REST API Specification: NexusMarket

## 1. Context & Architectural Role
In accordance with **Hexagonal Architecture (Ports and Adapters)**, the REST Controllers in `application.infrastructure.adapters.in.rest.controllers` serve as the **Inbound (Driving) Adapters**. They accept HTTP JSON requests, translate them to invocations of pure Java Atomic Domain Services in `application.domain.services`, and format domain responses into DTOs.

---

## 2. Global Error Handling
The `@RestControllerAdvice` [`GlobalExceptionHandler`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/infrastructure/adapters/in/rest/advice/GlobalExceptionHandler.java) translates pure domain exceptions to standard HTTP error responses:

| Domain Exception | HTTP Status Code | Description |
| :--- | :--- | :--- |
| `EntityNotFoundException` | `404 Not Found` | Entity requested by ID was not found |
| `InvalidDomainStateException` | `400 Bad Request` | Illegal business state transition or precondition failure |
| `DomainException` | `409 Conflict` | Duplicate unique constraint violation (SKU, Email, User ID) |
| `IllegalArgumentException` / `IllegalStateException` | `400 Bad Request` | Missing or invalid required parameters |
| `Exception` | `500 Internal Server Error` | Unexpected system failure |

### Error Response Schema
```json
{
  "timestamp": "2026-10-27T14:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with identifier: U-999",
  "path": "/api/users/U-999"
}
```

---

## 3. Resource Endpoints Catalog

### 1. User Management (`/api/users`)
* `POST /api/users/register`: Registers a new user (`BUYER`, `SELLER`, `ADMIN`, `OPERATOR_LOGISTIC`, `SUPERVISOR`). Returns `201 Created`.
* `POST /api/users/authenticate?email={email}`: Validates user status and returns identity details.
* `GET /api/users/{identifier}`: Retrieves user by unique ID.
* `PUT /api/users/{identifier}/block`: Transitions user status to `BLOCKED`. Returns `204 No Content`.
* `PUT /api/users/{identifier}/activate`: Transitions user status to `ACTIVE`. Returns `204 No Content`.

### 2. Seller Incorporation (`/api/sellers`)
* `POST /api/sellers/{sellerId}/incorporate`: Admin incorporates a pending seller account. Returns `204 No Content`.

### 3. Product Catalog (`/api/products`)
* `POST /api/products`: Registers a new product (`PHYSICAL` or `DIGITAL`) with unique SKU. Returns `201 Created`.
* `GET /api/products/{productId}`: Retrieves product details and variants.
* `PUT /api/products/{productId}/publish`: Transitions product to `PUBLISHED`.
* `PUT /api/products/{productId}/suspend`: Transitions product to `SUSPENDED`.
* `PUT /api/products/{productId}/discontinue`: Permanently transitions product to `DISCONTINUED`.
* `PUT /api/products/{productId}/price`: Updates unit price ($price > 0$).
* `POST /api/products/{productId}/variants`: Adds a variant attribute (e.g., Color, Size).

### 4. Warehouses (`/api/warehouses`)
* `POST /api/warehouses`: Admin registers a warehouse facility (`MARKETPLACE` or `SELLER`).
* `GET /api/warehouses/{warehouseId}`: Retrieves warehouse by ID.
* `PUT /api/warehouses/{warehouseId}/activate`: Activates warehouse.
* `PUT /api/warehouses/{warehouseId}/deactivate`: Deactivates warehouse.
* `PUT /api/warehouses/{warehouseId}/location`: Updates physical location.
* `GET /api/warehouses/owner/{ownerId}`: Queries all warehouses owned by an entity.

### 5. Inventory (`/api/inventory`)
* `POST /api/inventory/add`: Adds stock to a warehouse (`INFLOW`).
* `GET /api/inventory?productId={pId}&warehouseId={wId}`: Queries inventory levels.
* `GET /api/inventory/total-available/{productId}`: Returns total available units across all warehouses.
* `POST /api/inventory/reserve`: Reserves stock for orders (`RESERVE`).
* `POST /api/inventory/release`: Releases reserved stock back to available (`RETURN`).
* `POST /api/inventory/confirm-sale`: Deducts reserved stock on sale (`OUTFLOW_SALE`).
* `POST /api/inventory/mark-damaged`: Marks damaged units (`ADJUSTMENT`).

### 6. Shopping Cart (`/api/carts`)
* `GET /api/carts/{buyerId}`: Retrieves or creates buyer cart.
* `POST /api/carts/{buyerId}/items`: Adds product item with quantity accumulation.
* `DELETE /api/carts/{buyerId}/items/{productId}`: Removes item from cart.
* `DELETE /api/carts/{buyerId}`: Clears cart.

### 7. Orders & Checkout (`/api/orders`)
* `POST /api/orders/checkout/{buyerId}`: Converts cart into an Order starting in `PENDING_PAYMENT`.
* `POST /api/orders/{orderId}/pay`: Submits payment token to gateway, marks order `PAID`, sends email notification.
* `PUT /api/orders/{orderId}/cancel`: Cancels order.
* `GET /api/orders/{orderId}`: Retrieves order by ID.
* `GET /api/orders/buyer/{buyerId}`: Queries orders for a buyer.

### 8. Invoicing (`/api/invoices`)
* `POST /api/invoices`: Issues a legal tax invoice for a paid order.
* `GET /api/invoices/{invoiceId}`: Retrieves invoice by ID.
* `PUT /api/invoices/{invoiceId}/pay`: Marks invoice paid.
* `PUT /api/invoices/{invoiceId}/cancel`: Cancels invoice.

### 9. Logistics & Shipments (`/api/shipments`)
* `POST /api/shipments/prepare`: Prepares shipment for paid order from active warehouse.
* `GET /api/shipments/{shipmentId}`: Retrieves shipment by ID.
* `PUT /api/shipments/{shipmentId}/dispatch`: Assigns carrier and tracking number, marks order `DISPATCHED`.
* `PUT /api/shipments/{shipmentId}/confirm-delivery`: Finalizes delivery and transitions order to `DELIVERED_FINALIZED`.
* `PUT /api/shipments/{shipmentId}/report-failed`: Records delivery failure.

### 10. Returns & Refunds (`/api/returns`)
* `POST /api/returns/request`: Initiates return on delivered order.
* `GET /api/returns/{returnId}`: Retrieves return request by ID.
* `PUT /api/returns/{returnId}/approve`: Approves return.
* `PUT /api/returns/{returnId}/reject`: Rejects return.
* `POST /api/returns/process-refund`: Re-inspects item (restock vs damage) and executes gateway refund.

### 11. System Audit Logs (`/api/audit`)
* `GET /api/audit`: Retrieves all platform audit logs.
* `GET /api/audit/aggregate/{aggregateId}`: Retrieves audit logs for a specific target aggregate.

---

## 4. Terminal Execution & Verification
The platform can be demonstrated entirely from the terminal using the provided shell script:
```bash
# Terminal 1: Start application
mvn spring-boot:run

# Terminal 2: Run automated 24-step end-to-end curl demonstration
./test-api.sh
```
