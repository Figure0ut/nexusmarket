# Comprehensive Atomic Domain Services Specification

Domain Services in NexusMarket follow an **Atomic Single-Responsibility Pattern**. Rather than grouping multiple operations into fat services, each domain operation (both Commands and Queries) is encapsulated into its own dedicated, atomic service class under `application.domain.services`. All services are 100% pure Java without framework dependencies.

---

## Architecture Pattern

```
[ Inbound REST Adapters / Use Cases ]
                │
                ▼
[ Atomic Domain Services (`application.domain.services.<Action><Entity>Service`) ]
        ├── Models (`application.domain.models.*`)
        ├── Value Objects (`application.domain.valueobjects.*`)
        ├── Enums (`application.domain.enums.*`)
        └── Outbound Ports (`application.domain.ports.out.*`)
                │
                ▼
[ Infrastructure Persistence & Gateway Adapters ]
```

---

## Catalog of Atomic Domain Services by Business Domain

### 1. Invoicing & Billing ([Spec](./services/billing-invoicing-services.md))
- **`CreateInvoiceService`**: Generates and issues legal tax invoices for paid orders.
- **`GetInvoiceService`**: Queries an invoice by its identifier.
- **`MarkInvoicePaidService`**: Marks an issued invoice as paid.
- **`CancelInvoiceService`**: Cancels an issued invoice.

### 2. Shopping Cart ([Spec](./services/cart-management-services.md))
- **`GetOrCreateCartService`**: Retrieves or initializes a buyer's cart.
- **`AddItemToCartService`**: Adds items to a cart with automatic quantity accumulation.
- **`RemoveItemFromCartService`**: Removes an item line from a cart.
- **`ClearCartService`**: Clears all items from a buyer's cart.

### 3. Order & Checkout ([Spec](./services/order-checkout-services.md))
- **`CheckoutCartService`**: Converts an active cart into a pending order.
- **`ProcessOrderPaymentService`**: Coordinates payment gateway and buyer notification.
- **`CancelOrderService`**: Cancels an order.
- **`GetOrderService`**: Queries an order by ID.
- **`GetOrdersByBuyerService`**: Queries orders for a specific buyer.

### 4. Catalog Management ([Spec](./services/catalog-management-services.md))
- **`RegisterProductService`**: Enforces SKU and ID uniqueness on product creation.
- **`GetProductService`**: Queries a product by ID.
- **`PublishProductService`**: Transitions a product to `PUBLISHED`.
- **`SuspendProductService`**: Transitions a product to `SUSPENDED`.
- **`DiscontinueProductService`**: Permanently transitions a product to `DISCONTINUED`.
- **`UpdateProductPriceService`**: Updates a product's base price ($price > 0$).
- **`AddProductVariantService`**: Adds variant attributes to an existing product.

### 5. Inventory & Stock Allocation ([Spec](./services/inventory-allocation-services.md))
- **`GetInventoryService`**: Queries inventory for a product in a warehouse.
- **`AddStockService`**: Adds stock to available inventory (`INFLOW` movement).
- **`ReserveStockService`**: Reserves stock for pending orders (`RESERVE` movement).
- **`ReleaseStockReservationService`**: Returns reserved stock to available (`RETURN` movement).
- **`ConfirmSaleStockService`**: Deducts reserved stock on sale confirmation (`OUTFLOW_SALE` movement).
- **`MarkStockDamagedService`**: Marks damaged stock (`ADJUSTMENT` movement).
- **`GetTotalAvailableStockService`**: Computes total available stock across all warehouses.

### 6. Logistics & Fulfillment ([Spec](./services/logistics-dispatch-services.md))
- **`PrepareShipmentService`**: Prepares a shipment record for paid orders from an active warehouse.
- **`GetShipmentService`**: Queries a shipment by ID.
- **`DispatchShipmentService`**: Assigns carrier, tracking number, and dispatches shipment.
- **`ConfirmDeliveryService`**: Finalizes shipment and order delivery.
- **`ReportFailedDeliveryService`**: Records failed delivery attempts.

### 7. Returns & Refunds ([Spec](./services/return-refund-services.md))
- **`RequestReturnService`**: Initiates a return request on a finalized delivered order.
- **`GetReturnRequestService`**: Queries a return request by ID.
- **`ApproveReturnService`**: Approves a return request.
- **`RejectReturnService`**: Rejects a return request.
- **`ProcessReturnAndRefundService`**: Restocks/damages returned item and dispatches gateway refund.

### 8. Merchant Incorporation ([Spec](./services/seller-incorporation-services.md))
- **`IncorporateSellerService`**: Activates merchant accounts under exclusive `ADMIN` oversight.

### 9. User Authentication & Lifecycle ([Spec](./services/user-authentication-services.md))
- **`AuthenticateUserService`**: Verifies user identity and enforces non-blocked status.
- **`RegisterUserService`**: Registers users enforcing email and ID uniqueness.
- **`BlockUserService`**: Blocks a user account.
- **`ActivateUserService`**: Activates a user account.
- **`GetUserService`**: Queries a user by identifier.

### 10. Warehouse Management ([Spec](./services/warehouse-management-services.md))
- **`RegisterWarehouseService`**: Registers physical storage facilities under `ADMIN` oversight.
- **`GetWarehouseService`**: Queries a warehouse by ID.
- **`ActivateWarehouseService`**: Activates a warehouse for dispatch.
- **`DeactivateWarehouseService`**: Deactivates a warehouse.
- **`UpdateWarehouseLocationService`**: Updates warehouse address/location.
- **`GetWarehousesByOwnerService`**: Queries warehouses owned by an entity.

### 11. Operation Audit ([Spec](./services/operation-audit-services.md))
- **`LogOperationService`**: Appends an immutable audit entry.
- **`GetAllAuditLogsService`**: Queries all system audit logs.
- **`GetAuditLogsByAggregateService`**: Queries audit logs by target aggregate ID.

### 12. Security & Quality Controls ([Spec](./services/authorization-services.md))
- **`ValidateAuthorizationService`**: Enforces the formal Responsibility Matrix (RG-01, RG-02, RG-03) across all user roles.
