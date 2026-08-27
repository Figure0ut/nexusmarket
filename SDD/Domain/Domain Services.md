# Comprehensive Domain Services Specification

Domain Services in NexusMarket implement business operations that span across multiple aggregate roots or do not fit naturally inside a single entity.

---

## Catalog of Domain Services

### 1. User Authentication Domain Service
- **Role**: Coordinates user authentication, credential validation, and role-based privilege checks.
- **Rules**: Guarantees unique user ID and email across the system (RG-11). Enforces single role per user (RG-02).

### 2. Seller Incorporation Domain Service
- **Role**: Governs merchant onboarding.
- **Rules**: Enforces rule that sellers cannot self-register; incorporation is initiated exclusively by an ADMIN user (Dominio 3). Validates `TaxIdentifier` and updates seller state to `ACTIVE`.

### 3. Catalog Management Domain Service
- **Role**: Manages catalog publishing, price updates, and digital vs. physical product rules.
- **Rules**: Enforces $price > 0$, validates SKU uniqueness, and manages status transitions (`DRAFT` -> `PUBLISHED` -> `SUSPENDED` / `DISCONTINUED`).

### 4. Inventory Allocation Domain Service
- **Role**: Coordinates stock reservation across multiple warehouses for checkout orders.
- **Rules**: Enforces **Zero Negative Stock Rule**. Prohibits reserving non-existent or damaged stock.

### 5. Order Checkout Domain Service
- **Role**: Converts active shopping carts into pending orders and coordinates stock reservation.
- **Rules**: Enforces order state machine lifecycle. Finalized orders are non-modifiable.

### 6. Billing & Invoicing Domain Service
- **Role**: Issues legal commercial tax invoices upon order payment confirmation.
- **Rules**: Computes tax breakdown, validates buyer tax identifier and billing address.

### 7. Logistics & Dispatch Domain Service
- **Role**: Handles warehouse package pick, carrier assignment, tracking number generation, and delivery tracking for physical products.

### 8. Return & Refund Domain Service
- **Role**: Handles post-sale return authorization, item inspection, and refund processing.
- **Rules**: Validates return reasons and item condition (restock vs. damaged stock).

### 9. Operation Audit Domain Service
- **Role**: Logs administrative actions and system operations for reporting and tracking (OBJ-12).
