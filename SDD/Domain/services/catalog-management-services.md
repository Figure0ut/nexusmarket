# Catalog Management Domain Service Specification

## 1. Context & Business Purpose
The **`CatalogManagementService`** manages product listings, variant definitions, digital vs. physical item rules, and status lifecycles (OBJ-05 & Dominio 5).

---

## 2. Package Location & Dependencies
- **Package**: `application.domain.services`
- **Class**: [`CatalogManagementService`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/services/CatalogManagementService.java)
- **Dependencies**: [`ProductRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/ProductRepositoryPort.java)
- **Associated Models**: [`Product`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Product.java), [`ProductVariant`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/ProductVariant.java), [`SKU`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/SKU.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Money.java)

---

## 3. Core Business Invariants & Rules

1. **Product Type Classification**:
   - `PHYSICAL`: Requires inventory tracking across warehouses and physical logistics shipment.
   - `DIGITAL`: Instant post-payment delivery. Exempt from physical inventory allocation.
2. **Price Invariant**: Product price must be greater than zero (`price.isZero() == false`).
3. **SKU Uniqueness**: Every product must have a non-null, unique SKU code.
4. **Lifecycle State Machine**:
   - `DRAFT` -> `PUBLISHED` -> `SUSPENDED` / `DISCONTINUED`

---

## 4. Method Signatures

```java
public Product registerProduct(Product product);
public Product getProduct(String productId);
public void publishProduct(String productId);
public void suspendProduct(String productId);
public void discontinueProduct(String productId);
public void updatePrice(String productId, Money newPrice);
public void addVariant(String productId, ProductVariant variant);
```
