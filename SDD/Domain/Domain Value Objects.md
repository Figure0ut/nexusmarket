# Comprehensive Domain Value Objects Specification

Value Objects (VOs) in NexusMarket are **immutable**, **self-validating** domain representations defined by value equality rather than conceptual identity.

---

## 1. Shared Value Objects (`com.nexusmarket.domain.model.common`)

### A. [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java)
- **Purpose**: Represents prices, cart totals, invoice values, line item subtotals, and refund amounts.
- **Fields**: `BigDecimal amount`, `Currency currency`.
- **Invariants**: $amount \ge 0$. Scale fixed at 2 decimal places (`RoundingMode.HALF_UP`).
- **Arithmetic Methods**:
  - `add(Money other)`: Returns new `Money` with summed amounts. Checks matching currency.
  - `subtract(Money other)`: Returns new `Money` with subtracted amounts. Checks matching currency and non-negative result.
  - `multiply(int factor)`: Returns new `Money` scaled by integer factor.
  - `isGreaterThan(Money other)`: Compares monetary values.
  - `isZero()`: Returns true if amount is 0.00.

### B. [`Address`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Address.java)
- **Purpose**: Physical addresses for buyers, sellers, warehouses, billing, and shipments.
- **Fields**: `street`, `city`, `state`, `postalCode`, `country`.
- **Invariants**: Street and city cannot be null or empty string. Default country is "USA".

### C. [`Email`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Email.java)
- **Purpose**: Validated email address format across users, buyers, and sellers.
- **Fields**: `String value`.
- **Invariants**: Lowercase normalized, non-null, non-empty, contains `@` character, does not start or end with `@`.

### D. [`SKU`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/SKU.java)
- **Purpose**: Stock Keeping Unit identification for catalog items.
- **Fields**: `String code`.
- **Invariants**: Non-null, uppercase normalized.

### E. [`TaxIdentifier`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/TaxIdentifier.java)
- **Purpose**: Merchant corporate tax numbers (e.g. RUC, RFC, EIN, VAT).
- **Fields**: `String taxId`.
- **Invariants**: Non-null, non-empty, uppercase normalized.

---

## 2. Bounded Context Specific Value Objects

### A. [`StockQuantity`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/inventory/StockQuantity.java) (`inventory`)
- **Purpose**: Inventory stock counts for available, reserved, and damaged units.
- **Fields**: `int value`.
- **Invariants**: $value \ge 0$. Subtracting more than available throws `IllegalArgumentException`.
- **Operations**: `add(int delta)`, `subtract(int delta)`, `isZero()`.

### B. [`ProductVariant`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/catalog/ProductVariant.java) (`catalog`)
- **Purpose**: Option variants for physical products.
- **Fields**: `String name` (e.g. "Color"), `String value` (e.g. "Red").
- **Invariants**: Name and value cannot be null or empty.

---

## 3. Enumerated Value Objects (Enums)

1. **`UserRole`**: `BUYER`, `SELLER`, `OPERATOR_LOGISTIC`, `ADMIN`, `SUPERVISOR`.
2. **`UserStatus`**: `ACTIVE`, `BLOCKED`, `PENDING_INCORPORATION`.
3. **`WarehouseType`**: `MARKETPLACE`, `SELLER`.
4. **`ProductType`**: `PHYSICAL`, `DIGITAL`.
5. **`ProductStatus`**: `DRAFT`, `PUBLISHED`, `SUSPENDED`, `DISCONTINUED`.
6. **`InventoryMovementType`**: `INFLOW`, `RESERVE`, `OUTFLOW_SALE`, `ADJUSTMENT`, `RETURN`.
7. **`OrderStatus`**: `CART`, `PENDING_PAYMENT`, `PAID`, `DISPATCHED`, `DELIVERED_FINALIZED`, `CANCELLED`.
8. **`InvoiceStatus`**: `ISSUED`, `PAID`, `CANCELLED`.
9. **`ShipmentStatus`**: `PREPARING`, `IN_TRANSIT`, `DELIVERED`, `FAILED`.
10. **`ReturnReason`**: `DAMAGED`, `WRONG_ITEM`, `DEFECTIVE`, `CUSTOMER_REGRET`.
11. **`ReturnStatus`**: `REQUESTED`, `APPROVED`, `REJECTED`, `ITEM_RECEIVED`.
12. **`RefundStatus`**: `PENDING`, `PROCESSED`, `FAILED`.
