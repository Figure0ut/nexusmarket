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
- **Role**: Coordinates identity validation, user registration, account status management (`ACTIVE`, `BLOCKED`, `PENDING_INCORPORATION`), and email/ID uniqueness.

### 2. `SellerIncorporationService` ([Spec](./services/seller-incorporation-services.md))
- **Role**: Governs merchant onboarding. Enforces rule that sellers cannot self-register; incorporation is authorized exclusively by an `ADMIN` user.

### 3. `CatalogManagementService` ([Spec](./services/catalog-management-services.md))
- **Role**: Manages catalog publishing, price updates ($price > 0$), SKU uniqueness, variants, and product lifecycle (`DRAFT` -> `PUBLISHED` -> `SUSPENDED` / `DISCONTINUED`).

### 4. `WarehouseManagementService` ([Spec](./services/warehouse-management-services.md))
- **Role**: Fulfills OBJ-04 & Section 6.1. Controls physical storage spaces, merchant and marketplace warehouses, activation, and location management under `ADMIN` authorization.

### 5. `InventoryAllocationService` ([Spec](./services/inventory-allocation-services.md))
- **Role**: Coordinates stock reservation across multiple warehouses for checkout orders, enforcing the **Zero Negative Stock Rule**.

### 6. `OrderCheckoutService` ([Spec](./services/order-checkout-services.md))
- **Role**: Converts active shopping carts into pending orders, processes payments, coordinates order lifecycle, and handles cancellation.

### 7. `BillingInvoicingService` ([Spec](./services/billing-invoicing-services.md))
- **Role**: Issues legal commercial tax invoices upon order payment confirmation, computing tax identifiers and billing details.

### 8. `LogisticsDispatchService` ([Spec](./services/logistics-dispatch-services.md))
- **Role**: Handles warehouse package preparation, carrier assignment, tracking number generation, and delivery tracking.

### 9. `ReturnRefundService` ([Spec](./services/return-refund-services.md))
- **Role**: Handles post-sale return authorization, item inspection (restock reusable items vs. mark damaged), and refund processing.

### 10. `OperationAuditService` ([Spec](./services/operation-audit-services.md))
- **Role**: Logs administrative actions and system operations for reporting and tracking (OBJ-12).

### 11. `AuthorizationService` ([Spec](./services/authorization-services.md))
- **Role**: Enforces platform-wide quality controls, operational role constraints (RG-01, RG-02, RG-03), and the formal **Responsibility Matrix** (Section 12).

### 12. `CartManagementService` ([Spec](./services/cart-management-services.md))
- **Role**: Governs shopping cart operations prior to checkout, including auto-provisioning carts, adding items with quantity accumulation, item removal, and clearing carts.

