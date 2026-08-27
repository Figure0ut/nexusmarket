# Comprehensive Domain Model Specification

This specification documents the complete Aggregate Roots, Entities, Invariants, Constructors, and Business Methods across all 8 Bounded Contexts in NexusMarket.

---

## 1. User Bounded Context (`com.nexusmarket.domain.model.user`)

### A. `User` (Aggregate Root Entity)
Representing an authorized individual or system participant in NexusMarket.
- **Fields**:
  - `identifier` (String, final): Unique system user ID. Non-null, non-empty.
  - `fullName` (String): User's legal full name. Non-null, non-empty.
  - `email` ([`Email`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Email.java)): Validated RFC email address.
  - `role` ([`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/UserRole.java), final): User authorization role (`BUYER`, `SELLER`, `OPERATOR_LOGISTIC`, `ADMIN`, `SUPERVISOR`).
  - `status` ([`UserStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/user/UserStatus.java)): Account state (`ACTIVE`, `BLOCKED`, `PENDING_INCORPORATION`).
- **Rich Constructor**: `User(String identifier, String fullName, Email email, UserRole role, UserStatus status)`
  - *Validation*: Throws `IllegalArgumentException` if any field is null, or if identifier/fullName are empty strings.
- **Business Methods**:
  - `changeStatus(UserStatus newStatus)`: Validates `newStatus != null`, updates user status.
  - `updateContactInfo(Email newEmail)`: Validates `newEmail != null`, updates contact email.

### B. `Buyer` (Domain Entity - extends `User`)
Representing purchasing consumers in the marketplace.
- **Fields**:
  - `mainAddress` ([`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Address.java)): Primary delivery location.
  - `additionalAddresses` (`List<Address>`): Secondary shipping locations.
  - `commercialStatus` (String): Commercial classification (e.g. "STANDARD", "PREMIUM", "VIP").
- **Rich Constructor**: `Buyer(String identifier, String fullName, Email email, UserRole role, UserStatus status, Address mainAddress, List<Address> additionalAddresses, String commercialStatus)`
  - *Convenience Constructor*: `Buyer(String identifier, String fullName, String emailStr, String mainAddressStr, String commercialStatus)` (Defaults `role` to `BUYER`, `status` to `ACTIVE`).
- **Business Methods**:
  - `updateMainAddress(Address address)`: Updates primary delivery address.
  - `addSecondaryAddress(Address address)`: Validates address is non-null and not duplicate of `mainAddress`, appends to secondary list.
  - `validateOperation(User target)`: Security invariant check. Throws `IllegalStateException` if a buyer attempts to administer another buyer or user.

### C. `Seller` (Domain Entity - extends `User`)
Representing merchant sellers offering products on NexusMarket.
- **Fields**:
  - `taxIdentifier` ([`TaxIdentifier`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/TaxIdentifier.java)): Merchant corporate tax ID.
  - `corporateName` (String): Legal corporate company name.
- **Rich Constructor**: `Seller(String identifier, String fullName, Email email, UserRole role, UserStatus status, TaxIdentifier taxIdentifier, String corporateName)`
  - *Default State*: Status is initialized as `PENDING_INCORPORATION`.
- **Business Methods**:
  - `incorporate(User adminUser)`: Enforces business rule that seller incorporation must be executed exclusively by a user with `ADMIN` role. Throws `IllegalStateException` if `adminUser.getRole() != UserRole.ADMIN`. Updates status to `ACTIVE`.

---

## 2. Warehouse Bounded Context (`com.nexusmarket.domain.model.warehouse`)

### `Warehouse` (Aggregate Root Entity)
Representing physical storage and fulfillment centers.
- **Fields**:
  - `warehouseId` (String, final): Unique warehouse ID.
  - `name` (String): Descriptive warehouse name.
  - `location` ([`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Address.java)): Physical address of the warehouse.
  - `type` ([`WarehouseType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/warehouse/WarehouseType.java), final): Classification (`MARKETPLACE` vs `SELLER`).
  - `ownerId` (String, final): Seller ID or Admin owner ID.
  - `active` (boolean): Operational availability state.
- **Rich Constructor**: `Warehouse(String warehouseId, String name, Address location, WarehouseType type, String ownerId, boolean active)`
- **Business Methods**:
  - `updateLocation(Address newLocation)`, `updateName(String newName)`, `activate()`, `deactivate()`.

---

## 3. Catalog Bounded Context (`com.nexusmarket.domain.model.catalog`)

### `Product` (Aggregate Root Entity)
Representing items offered in the central marketplace catalog.
- **Fields**:
  - `productId` (String, final), `sku` ([`SKU`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/SKU.java), final), `sellerId` (String, final).
  - `name` (String), `description` (String), `price` ([`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java)).
  - `productType` ([`ProductType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/catalog/ProductType.java), final): `PHYSICAL` (requires stock & logistics) vs `DIGITAL` (immediate delivery).
  - `status` ([`ProductStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/catalog/ProductStatus.java)): `DRAFT`, `PUBLISHED`, `SUSPENDED`, `DISCONTINUED`.
  - `variants` (`List<ProductVariant>`): List of variant options (e.g. Color, Size).
- **Rich Constructor**: `Product(...)`
  - *Invariants*: `price` must be $> 0$.
- **Business Methods**: `publish()`, `suspend()`, `discontinue()`, `updatePrice(Money newPrice)`, `addVariant(ProductVariant variant)`.

---

## 4. Distributed Inventory Bounded Context (`com.nexusmarket.domain.model.inventory`)

### `Inventory` (Aggregate Root Entity)
Tracking distributed physical stock per product and warehouse.
- **Fields**:
  - `inventoryId` (String, final), `productId` (String, final), `warehouseId` (String, final).
  - `availableStock` ([`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/inventory/StockQuantity.java)): Units available for sale.
  - `reservedStock` ([`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/inventory/StockQuantity.java)): Units locked in pending checkout orders.
  - `damagedStock` ([`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/inventory/StockQuantity.java)): Defective or damaged units.
- **Invariants**: **Zero Negative Stock Rule** ($availableStock \ge 0$, $reservedStock \ge 0$).
- **Business Methods**:
  - `addStock(int qty)`: Increases available stock.
  - `reserveStock(int qty)`: Validates `availableStock >= qty`, transfers units to `reservedStock`.
  - `confirmSale(int qty)`: Deducts units from `reservedStock` upon payment confirmation.
  - `releaseReservation(int qty)`: Restores reserved units back to `availableStock`.
  - `markAsDamaged(int qty)`: Transfers units from `availableStock` to `damagedStock`.

---

## 5. Cart & Order Bounded Context (`com.nexusmarket.domain.model.order`)

### A. `Cart` (Aggregate Root Entity) & `CartItem`
- **Fields**: `cartId`, `buyerId`, `items` (`List<CartItem>`).
- **Methods**: `addItem()`, `removeItem()`, `clear()`, `calculateTotal()`.

### B. `Order` (Aggregate Root Entity) & `OrderItem`
- **Fields**: `orderId`, `buyerId`, `items` (`List<OrderItem>`), `status` ([`OrderStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/order/OrderStatus.java)), `shippingAddress` ([`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Address.java)), `totalAmount` ([`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java)).
- **State Machine**: `CART` -> `PENDING_PAYMENT` -> `PAID` -> `DISPATCHED` -> `DELIVERED_FINALIZED`.
- **Invariants**: Finalized/Delivered orders are immutable and cannot be cancelled or modified under any circumstance.
- **Business Methods**: `markAsPaid()`, `markAsDispatched()`, `finalizeDelivery()`, `cancel()`.

---

## 6. Billing Bounded Context (`com.nexusmarket.domain.model.billing`)

### `Invoice` (Aggregate Root Entity)
- **Fields**: `invoiceId`, `orderId`, `buyerId`, `taxIdentifier`, `billingAddress`, `totalAmount`, `status` ([`InvoiceStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/billing/InvoiceStatus.java)).
- **Methods**: `markAsPaid()`, `cancel()`.

---

## 7. Logistics Bounded Context (`com.nexusmarket.domain.model.logistics`)

### `Shipment` (Aggregate Root Entity)
- **Fields**: `shipmentId`, `orderId`, `warehouseId`, `destinationAddress`, `carrier`, `trackingNumber`, `status` ([`ShipmentStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/logistics/ShipmentStatus.java)).
- **Methods**: `dispatch(carrier, trackingNumber)`, `confirmDelivery()`, `markAsFailed()`.

---

## 8. Returns & Refunds Bounded Context (`com.nexusmarket.domain.model.returns`)

### A. `ReturnRequest` (Aggregate Root Entity)
- **Fields**: `returnId`, `orderId`, `buyerId`, `productId`, `reason` ([`ReturnReason`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/ReturnReason.java)), `status` ([`ReturnStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/ReturnStatus.java)).
- **Methods**: `approve()`, `reject()`, `confirmItemReceived()`.

### B. `Refund` (Aggregate Root Entity)
- **Fields**: `refundId`, `returnId`, `buyerId`, `amount` ([`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java)), `status` ([`RefundStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/returns/RefundStatus.java)).
- **Methods**: `process()`, `fail()`.
