# Catalog Management Domain Service Specification

## 1. Context & Business Purpose
The **Catalog Management Domain Service** manages product listings, variant definitions, digital vs. physical item rules, and status lifecycles (OBJ-05 & Dominio 5).

---

## 2. Domain Organizations & Package Location
- **Package**: `com.nexusmarket.domain.service.catalog`
- **Associated Aggregate Roots**: [`Product`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/catalog/Product.java)
- **Associated Value Objects**: [`SKU`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/SKU.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/common/Money.java), [`ProductVariant`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/catalog/ProductVariant.java), [`ProductType`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/catalog/ProductType.java), [`ProductStatus`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/com/nexusmarket/domain/model/catalog/ProductStatus.java)

---

## 3. Core Business Invariants & Rules

1. **Product Type Classification**:
   - `PHYSICAL`: Requires inventory tracking across warehouses and physical logistics shipment.
   - `DIGITAL`: Instant post-payment delivery. Exempt from physical inventory allocation.
2. **Price Invariant**: Product price must be greater than zero (`price.isZero() == false`).
3. **SKU Uniqueness**: Every product must have a non-null, uppercase normalized SKU.
4. **Lifecycle State Machine**:
   - `DRAFT` -> `PUBLISHED` -> `SUSPENDED` / `DISCONTINUED`
   - A `DISCONTINUED` product cannot be republished.

---

## 4. Key Constructor Validation & Logic

```java
public Product(String productId, SKU sku, String sellerId, String name, String description,
               Money price, ProductType productType, ProductStatus status, List<ProductVariant> variants) {
    // Validates productId, sku, sellerId, name non-empty
    // Validates price > 0
    // Enforces immutable variant list
}
```
