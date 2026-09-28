# Comprehensive Domain Services Specification

Domain Services in NexusMarket implement business logic and workflows that cross multiple aggregate roots or require coordination across outbound ports. All domain services reside in package `application.domain.services` and are 100% pure Java.

---

## Architecture Pattern

```
[ Inbound Adapters / Use Cases ]
               │
               ▼
[ Domain Services (`application.domain.services.*`) ]
       ├── Models (`application.domain.models.*`)
       ├── Value Objects (`application.domain.valueobjects.*`)
       ├── Enums (`application.domain.enums.*`)
       └── Outbound Ports (`application.domain.ports.out.*`)
               │
               ▼
[ Infrastructure Persistence & Gateway Adapters ]
```

---

## Catalog of Domain Services

### 1. `UserAuthenticationService` ([Spec](./services/user-authentication-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `UserRepositoryPort`
- **Operations**:
  - `authenticate(Email email)`: Verifies user exists and status is `ACTIVE` (rejects `BLOCKED` or `PENDING_INCORPORATION`).
  - `registerUser(User user)`: Ensures uniqueness of identifier and email.
  - `blockUser(String identifier)`: Suspends user account.
  - `activateUser(String identifier)`: Activates user account.

### 2. `SellerIncorporationService` ([Spec](./services/seller-incorporation-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `UserRepositoryPort`
- **Operations**:
  - `incorporateSeller(String sellerId, User adminUser)`: Enforces business rule that seller incorporation into the active marketplace can only be authorized by an `ADMIN` user.
  - `incorporateSeller(Seller seller, User adminUser)`: Direct incorporation validation and persistence.

### 3. `CatalogManagementService` ([Spec](./services/catalog-management-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `ProductRepositoryPort`
- **Operations**:
  - `registerProduct(Product product)`: Validates product and verifies SKU uniqueness.
  - `publishProduct(String productId)`: Transitions status from `DRAFT` to `PUBLISHED`.
  - `suspendProduct(String productId)`: Transitions status to `SUSPENDED`.
  - `discontinueProduct(String productId)`: Transitions status to `DISCONTINUED`.
  - `updatePrice(String productId, Money newPrice)`: Updates product price.
  - `addVariant(String productId, ProductVariant variant)`: Appends variant options.

### 4. `InventoryAllocationService` ([Spec](./services/inventory-allocation-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `InventoryRepositoryPort`, `WarehouseRepositoryPort`
- **Operations**:
  - `addStock(String inventoryId, String productId, String warehouseId, int quantity)`: Validates active warehouse, increments available stock.
  - `reserveStock(String productId, String warehouseId, int quantity)`: Validates available stock, locks units into reserved stock.
  - `confirmSale(String productId, String warehouseId, int quantity)`: Deducts sold units from reserved stock.
  - `releaseReservation(String productId, String warehouseId, int quantity)`: Releases reserved stock back to available stock.
  - `markStockAsDamaged(String productId, String warehouseId, int quantity)`: Transfers stock to damaged stock.
  - `getTotalAvailableStock(String productId)`: Aggregates available stock across all warehouses.

### 5. `OrderCheckoutService` ([Spec](./services/order-checkout-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `OrderRepositoryPort`, `PaymentGatewayPort`, `NotificationPort`
- **Operations**:
  - `checkoutCart(String buyerId, String orderId, Address shippingAddress)`: Converts active cart items into formal `Order` in `PENDING_PAYMENT`, clears cart.
  - `processOrderPayment(String orderId, String paymentToken, Email buyerEmail)`: Validates payment via gateway, transitions order to `PAID`, triggers confirmation notification.
  - `cancelOrder(String orderId)`: Cancels order if not finalized.

### 6. `BillingInvoicingService` ([Spec](./services/billing-invoicing-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `InvoiceRepositoryPort`, `OrderRepositoryPort`
- **Operations**:
  - `generateInvoice(String invoiceId, String orderId, String buyerId, TaxIdentifier taxIdentifier, Address billingAddress)`: Generates legal tax invoice for `PAID` orders.
  - `markInvoicePaid(String invoiceId)`: Updates invoice status to `PAID`.
  - `cancelInvoice(String invoiceId)`: Updates invoice status to `CANCELLED`.

### 7. `LogisticsDispatchService` ([Spec](./services/logistics-dispatch-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `ShipmentRepositoryPort`, `OrderRepositoryPort`, `WarehouseRepositoryPort`
- **Operations**:
  - `prepareShipment(String shipmentId, String orderId, String warehouseId, Address destinationAddress)`: Initiates physical dispatch preparation for paid orders from an active warehouse.
  - `dispatchShipment(String shipmentId, String carrier, String trackingNumber)`: Assigns carrier/tracking, marks shipment `IN_TRANSIT` and order `DISPATCHED`.
  - `confirmDelivery(String shipmentId)`: Confirms delivery, marks shipment `DELIVERED` and order `DELIVERED_FINALIZED`.
  - `reportFailedDelivery(String shipmentId)`: Marks shipment as `FAILED`.

### 8. `ReturnRefundService` ([Spec](./services/return-refund-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `ReturnRepositoryPort`, `OrderRepositoryPort`, `InventoryRepositoryPort`, `PaymentGatewayPort`
- **Operations**:
  - `requestReturn(String returnId, String orderId, String buyerId, String productId, ReturnReason reason)`: Creates return request for delivered orders.
  - `approveReturn(String returnId)`: Approves customer return.
  - `rejectReturn(String returnId)`: Rejects customer return.
  - `processReturnedItemAndRefund(String refundId, String returnId, String warehouseId, boolean isReusable, Money refundAmount)`: Inspects received item (restocks or marks damaged), processes monetary refund via payment gateway.

### 9. `OperationAuditService` ([Spec](./services/operation-audit-services.md))
- **Package**: `application.domain.services`
- **Dependencies**: `AuditLogRepositoryPort`
- **Operations**:
  - `logOperation(String entryId, String actorId, UserRole role, String action, String targetAggregateId, String details)`: Appends immutable audit record.
  - `getAllLogs()`: Retrieves complete administrative audit log history.
  - `getLogsForAggregate(String aggregateId)`: Queries audit trail for specific aggregate.
