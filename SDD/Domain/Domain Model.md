# Comprehensive Domain Model Specification

This specification documents the complete Domain Entities & Aggregate Roots located in package `application.domain.models`.

---

## Package: `application.domain.models`

### 1. `User`
- **Fields**: `identifier`, `fullName`, `email` ([`Email`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Email.java)), `role` ([`UserRole`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserRole.java)), `status` ([`UserStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/UserStatus.java)).
- **Design**: Default constructor, overloaded constructor, getters and setters for all properties with validation.

### 2. `Buyer` (Extends `User`)
- **Fields**: `mainAddress` ([`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Address.java)), `additionalAddresses` (`List<Address>`), `commercialStatus`.
- **Design**: Getters and setters for all properties, convenience methods (`addSecondaryAddress`, `updateMainAddress`, `validateOperation`).

### 3. `Seller` (Extends `User`)
- **Fields**: `taxIdentifier` ([`TaxIdentifier`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/TaxIdentifier.java)), `corporateName`.
- **Design**: Getters and setters, `incorporate(User adminUser)` business method validating ADMIN role.

### 4. `Warehouse`
- **Fields**: `warehouseId`, `name`, `location` ([`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Address.java)), `type` ([`WarehouseType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/WarehouseType.java)), `ownerId`, `active`.
- **Design**: Getters and setters, activation/deactivation business methods.

### 5. `Product`
- **Fields**: `productId`, `sku` ([`SKU`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/SKU.java)), `sellerId`, `name`, `description`, `price` ([`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Money.java)), `productType` ([`ProductType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/ProductType.java)), `status` ([`ProductStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/ProductStatus.java)), `variants` (`List<ProductVariant>`).
- **Design**: Getters and setters, lifecycle methods (`publish`, `suspend`, `discontinue`).

### 6. `Inventory`
- **Fields**: `inventoryId`, `productId`, `warehouseId`, `availableStock` ([`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/StockQuantity.java)), `reservedStock`, `damagedStock`.
- **Design**: Getters and setters, stock management methods (`reserveStock`, `confirmSale`, `releaseReservation`, `markAsDamaged`).

### 7. `Cart` & `CartItem`
- **Fields**: `cartId`, `buyerId`, `items` (`List<CartItem>`).
- **Design**: Getters and setters, `addItem`, `removeItem`, `calculateTotal`.

### 8. `Order` & `OrderItem`
- **Fields**: `orderId`, `buyerId`, `items` (`List<OrderItem>`), `status` ([`OrderStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/OrderStatus.java)), `shippingAddress`, `totalAmount`.
- **Design**: Getters and setters, state transition methods (`markAsPaid`, `markAsDispatched`, `finalizeDelivery`, `cancel`).

### 9. `Invoice`
- **Fields**: `invoiceId`, `orderId`, `buyerId`, `taxIdentifier`, `billingAddress`, `totalAmount`, `status` ([`InvoiceStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/InvoiceStatus.java)).
- **Design**: Getters and setters, `markAsPaid`, `cancel`.

### 10. `Shipment`
- **Fields**: `shipmentId`, `orderId`, `warehouseId`, `destinationAddress`, `carrier`, `trackingNumber`, `status` ([`ShipmentStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/ShipmentStatus.java)).
- **Design**: Getters and setters, `dispatch`, `confirmDelivery`, `markAsFailed`.

### 11. `ReturnRequest` & `Refund`
- **Fields**: `returnId`, `orderId`, `buyerId`, `productId`, `reason` ([`ReturnReason`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/ReturnReason.java)), `status` ([`ReturnStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/ReturnStatus.java)), `refundId`, `amount`, `refundStatus` ([`RefundStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/enums/RefundStatus.java)).
- **Design**: Getters and setters, lifecycle approval and processing methods.
