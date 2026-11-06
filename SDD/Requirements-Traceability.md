# Requirements Traceability Matrix

Maps every statement of the functional specification ("Especificación Funcional del Negocio - NexusMarket") to its implementation.
The specification is treated as absolute: nothing outside it was added to the business rules, and enumerated lists (states, movements, roles, matrix) are implemented exactly.

## 1. Objectives (section 2)

| Code | Objective | Implementation |
| :--- | :--- | :--- |
| OBJ-01 | Manage information of all users | `User`, `RegisterUserService`, `GetAllUsersService`, `Block/ActivateUserService` · `/api/users` |
| OBJ-02 | Seller registration and administration | `Seller`, `IncorporateSellerService` · `POST /api/sellers` |
| OBJ-03 | Registered buyers | `Buyer`, buyer self-registration, address services · `/api/users/register` |
| OBJ-04 | Warehouse information | `Warehouse`, warehouse services · `/api/warehouses` |
| OBJ-05 | Product catalog | `Product`, catalog services · `/api/products` |
| OBJ-06 | Distributed inventory | `Inventory`, inventory services · `/api/inventory` |
| OBJ-07 | Shopping cart | `Cart`, cart services · `/api/carts` |
| OBJ-08 | Full order lifecycle | `Order`, `CheckoutCartService`, `ProcessOrderPaymentService`, logistics services · `/api/orders` |
| OBJ-09 | Billing | `Invoice`, `CreateInvoiceService` (issued at payment) · `/api/invoices` |
| OBJ-10 | Logistics | `Shipment`, `Prepare/Dispatch/ConfirmDelivery` services · `/api/shipments` |
| OBJ-11 | Returns and refunds | `ReturnRequest`, `Refund`, return services · `/api/returns` |
| OBJ-12 | Administrative information for consultation | `GenerateAdministrativeReportService` · `/api/reports/summary`; audit trail · `/api/audit` |

## 2. Business flow (section 6.1)

| Step | Implementation |
| :--- | :--- |
| 1. Incorporation: the Administrator registers the seller and its first warehouse | `POST /api/sellers` (seller + `firstWarehouse` in a single operation) |
| 2. Catalog: the seller registers products | `POST /api/products` (seller = authenticated user) |
| 3. Inventory: initial stock in the associated warehouses | `POST /api/inventory/add` (INFLOW) |
| 4. Publication: products become visible in the public catalog | `PUT /api/products/{id}/publish`; `GET /api/products` lists PUBLISHED only |
| 5. Purchase: cart and order confirmation | `POST /api/carts/{buyerId}/items`, `POST /api/orders/checkout/{buyerId}` (RESERVE) |
| 6. Transaction: payment validated, preparation starts | `POST /api/orders/{id}/pay` → PAID + invoice + notification |
| 7. Logistics: packing, dispatch, transport | `POST /api/shipments/prepare`, `PUT /api/shipments/{id}/dispatch` (OUTFLOW_SALE) |
| 8. Closing: finalized after confirmed delivery | `PUT /api/shipments/{id}/confirm-delivery` → DELIVERED_FINALIZED |

## 3. Domains

| Domain | Statement | Implementation |
| :--- | :--- | :--- |
| 1 Users | Identifier unique; full name not empty; email unique; role unique per user; status from a catalog | `User` setters; `RegisterUserService`; DB unique constraints; `UserStatus` |
| 2 Buyers | Main address (mandatory), additional addresses (optional), commercial status (mandatory) | `Buyer` |
| 2 Buyers | A buyer never manages other buyers' information nor inventories | `validateOwnership`; inventory endpoints forbid BUYER |
| 3 Sellers | Sellers cannot self-register; incorporated by the Administrator | `/api/users/register` creates buyers only; `/api/users` rejects SELLER; `POST /api/sellers` ADMIN only |
| 4 Warehouses | Marketplace warehouses and seller warehouses | `WarehouseType.MARKETPLACE / SELLER` |
| 5 Catalog | Physical (inventory + dispatch) vs digital (immediate delivery after payment) | `AddStockService` rejects digital; `Order.markAsPaid` finalizes digital-only orders; shipments only for physical |
| 5 Catalog | Variants (list); status Published / Suspended / Discontinued | `ProductVariant` list; `ProductStatus` has exactly 3 values |
| 6 Inventory | Linked to a product and a warehouse | `AddStockService` validates both exist |
| 6 Inventory | Movements: inflow, reservation, sale outflow, adjustment, return | `InventoryMovementType` (5 values) used by add / checkout / dispatch / mark-damaged / refund |
| 6 Inventory | Never negative stock | `StockQuantity`, `Inventory` guards |
| 7 Orders | Lifecycle Cart → Pending payment → Paid → Dispatched → Delivered/Finalized | `OrderStatus` (5 values) and `Order` transitions |

## 4. Restrictions and validations (sections 10-12)

| Code | Statement | Implementation |
| :--- | :--- | :--- |
| RG-01 | Every operation is executed by an authenticated user | `X-User-Email` header resolved by `AuthenticatedUserResolver` + `AuthenticateUserService` (ACTIVE only) → 401 otherwise |
| RG-02 | One role per user | single `role` field; `Buyer`/`Seller` lock their role |
| RG-03 | No participant manages information outside its role | `ValidateAuthorizationService` (matrix + ownership) → 403 |
| §11 | Cannot reserve inexistent or damaged inventory | `CheckoutCartService` only reserves available, non-damaged stock in active warehouses |
| §11 | A finalized order cannot be modified | `Order.ensureNotFinalized()`; returns are separate aggregates |
| §11 | Identity document and email unique | `RegisterUserService` + unique columns |
| §12 | Responsibility matrix | `ValidateAuthorizationService.RESPONSIBILITY_MATRIX` (exact copy) |

## 5. Interpretations (points the specification does not define)

These are the only decisions not literally written in the specification; each one is the minimum needed to make the system operable.

1. **Authentication mechanism**: out of scope (section 3.2) → users are identified by their email (the "main access channel") in a header.
2. **Bootstrap Administrator**: RG-01 requires an authenticated user and only an Administrator can create sellers/internal users, so one Administrator is created at startup.
3. **Buyer self-registration** is the only operation (with login) without an authenticated user, because it creates the account.
4. **Processes missing from the matrix** (warehouses, users, cart, shipments, reports) are assigned from the participant descriptions of section 5.
5. **Supervisor** consults every process but executes none ("consultation and operational follow-up profile").
6. **Invoice** is issued automatically when the payment is validated; its only status is `ISSUED`.
7. **Return / refund workflow** (request → approve/reject → refund) and shipment states (`PREPARING`, `IN_TRANSIT`, `DELIVERED`) are the minimum to execute the included processes; no cancellation or failure states were added.
