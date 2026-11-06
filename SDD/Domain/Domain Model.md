# Domain Model Specification

Entities and aggregates of package `application.domain.models` (100% pure Java, no framework annotations).
Invariants are validated inside constructors, setters and business methods.

| Model | Specification source | Key fields | Business methods / invariants |
| :--- | :--- | :--- | :--- |
| `User` | Domain 1 | `identifier`, `identityDocument`, `fullName`, `email`, `role`, `status` | All mandatory; full name not empty; `isActive()`. Uniqueness of identifier, identity document and email is enforced by `RegisterUserService`. |
| `Buyer` (extends `User`) | Domain 2 | `mainAddress` (mandatory), `additionalAddresses` (optional), `commercialStatus` (mandatory) | Role always `BUYER`; `updateMainAddress`, `addSecondaryAddress`, `validateOperation` (never operates on other buyers). |
| `Seller` (extends `User`) | Domain 3 | `taxIdentifier`, `corporateName` | Role always `SELLER`; `incorporate(admin)`: only an ACTIVE ADMIN, `PENDING_INCORPORATION` → `ACTIVE`. |
| `Warehouse` | Domain 4 | `warehouseId`, `name`, `location`, `type` (`MARKETPLACE`/`SELLER`), `ownerId`, `active` | Marketplace warehouses are owned by `NEXUSMARKET`; `activate`, `deactivate`, `updateLocation`. |
| `Product` | Domain 5 | `productId`, `sku`, `sellerId`, `name`, `description`, `price`, `productType`, `status`, `variants` | `publish`, `suspend`, `discontinue` (final), `updatePrice` (> 0), `addVariant`, `isPublished`, `isPhysical`. |
| `Inventory` | Domain 6 | `productId`, `warehouseId`, `availableStock`, `reservedStock`, `damagedStock`, `lastMovementType` | `addStock` (INFLOW), `reserveStock` (RESERVE), `confirmSale` (OUTFLOW_SALE), `markAsDamaged` (ADJUSTMENT), `registerReturn` (RETURN). Never negative; damaged stock is never reserved. |
| `Cart` / `CartItem` | Domain 7 (stage CART), OBJ-07 | `cartId`, `buyerId`, `items` | `getStatus()` = `CART`; `addItem` (accumulates quantity), `removeItem`, `clear`, `calculateTotal`. |
| `Order` / `OrderItem` | Domain 7 | `orderId`, `buyerId`, `items` (product, type, reserving warehouse, unit price, quantity), `status`, `shippingAddress`, `totalAmount` | `markAsPaid` (digital-only orders are finalized immediately), `markAsDispatched`, `finalizeDelivery`; a finalized order can never be modified. |
| `Invoice` | OBJ-09 | `invoiceId`, `orderId`, `buyerId`, `taxIdentifier`, `billingAddress`, `totalAmount`, `status` | Issued once the payment is validated. |
| `Shipment` | OBJ-10 | `shipmentId`, `orderId`, `warehouseId`, `destinationAddress`, `carrier`, `trackingNumber`, `status` | `dispatch` (only from PREPARING), `confirmDelivery` (only from IN_TRANSIT). |
| `ReturnRequest` / `Refund` | OBJ-11 | `returnId`, `orderId`, `buyerId`, `productId`, `reason`, `status`; `refundId`, `amount`, `status` | `approve`, `reject`, `confirmItemReceived`; `process`, `fail`. |
| `AuditEntry` | Traceability (section 1) | `entryId`, `actorId`, `role`, `action`, `targetAggregateId`, `timestamp`, `details` | Immutable operation record. |
| `AdministrativeReport` | OBJ-12 | Counters and totals | Read-only consolidated snapshot. |
