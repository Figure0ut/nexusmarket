# Comprehensive Domain Value Objects & Enums Specification

Value Objects and Enums are organized into package `application.domain.valueobjects` and package `application.domain.enums`.

---

## 1. Package: `application.domain.valueobjects`

All non-enum Value Objects feature default and overloaded constructors, getters, setters, and self-validation.

- **[`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Money.java)**: `amount` (BigDecimal $\ge 0$), `currency` (Currency). Getters, setters, `add()`, `subtract()`, `multiply()`.
- **[`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Address.java)**: `street`, `city`, `state`, `postalCode`, `country`. Getters and setters.
- **[`Email`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Email.java)**: `value` (RFC format validation). Getter and setter.
- **[`SKU`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/SKU.java)**: `code` (Stock Keeping Unit). Getter and setter.
- **[`TaxIdentifier`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/TaxIdentifier.java)**: `taxId` (Corporate tax identifier). Getter and setter.
- **[`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/StockQuantity.java)**: `value` (Integer quantity $\ge 0$). Getter and setter.
- **[`ProductVariant`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/ProductVariant.java)**: `name`, `value` pair. Getters and setters.

---

## 2. Package: `application.domain.enums`

- **`UserRole`**: `BUYER`, `SELLER`, `OPERATOR_LOGISTIC`, `ADMIN`, `SUPERVISOR`.
- **`UserStatus`**: `ACTIVE`, `BLOCKED`, `PENDING_INCORPORATION`.
- **`WarehouseType`**: `MARKETPLACE`, `SELLER`.
- **`ProductType`**: `PHYSICAL`, `DIGITAL`.
- **`ProductStatus`**: `DRAFT`, `PUBLISHED`, `SUSPENDED`, `DISCONTINUED`.
- **`InventoryMovementType`**: `INFLOW`, `RESERVE`, `OUTFLOW_SALE`, `ADJUSTMENT`, `RETURN`.
- **`OrderStatus`**: `CART`, `PENDING_PAYMENT`, `PAID`, `DISPATCHED`, `DELIVERED_FINALIZED`, `CANCELLED`.
- **`InvoiceStatus`**: `ISSUED`, `PAID`, `CANCELLED`.
- **`ShipmentStatus`**: `PREPARING`, `IN_TRANSIT`, `DELIVERED`, `FAILED`.
- **`ReturnReason`**: `DAMAGED`, `WRONG_ITEM`, `DEFECTIVE`, `CUSTOMER_REGRET`.
- **`ReturnStatus`**: `REQUESTED`, `APPROVED`, `REJECTED`, `ITEM_RECEIVED`.
- **`RefundStatus`**: `PENDING`, `PROCESSED`, `FAILED`.
