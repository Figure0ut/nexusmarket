# Catalog Management Atomic Domain Services Specification

## 1. Context & Business Purpose
The Catalog Management domain governs product registration, validation, price updates, variants, and catalog lifecycle states (`DRAFT`, `PUBLISHED`, `SUSPENDED`, `DISCONTINUED`). Each operation is implemented as an atomic domain service.

---

## 2. Package Location & Atomic Services
- **Package**: `application.domain.services`
- **Dependencies**: [`ProductRepositoryPort`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/ports/out/ProductRepositoryPort.java)
- **Associated Models**: [`Product`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/models/Product.java), [`SKU`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/SKU.java), [`Money`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/Money.java), [`ProductVariant`](file:///Users/pablo/Documents/Uni/nexusmarket/src/main/java/application/domain/valueobjects/ProductVariant.java)

### Services
1. **`RegisterProductService`**: Enforces SKU and product ID uniqueness on registration.
2. **`GetProductService`**: Retrieves a product by ID.
3. **`PublishProductService`**: Transitions a product to `PUBLISHED`.
4. **`SuspendProductService`**: Transitions a product to `SUSPENDED`.
5. **`DiscontinueProductService`**: Transitions a product to `DISCONTINUED`.
6. **`UpdateProductPriceService`**: Updates the product unit price ($price > 0$).
7. **`AddProductVariantService`**: Adds a new product variant.

---

## 3. Core Business Invariants & Rules

1. **SKU Uniqueness**: Every product must possess a globally unique SKU.
2. **Positive Pricing**: Prices must always be strictly greater than zero ($price > 0$).
3. **Valid State Transitions**: A product cannot be modified once `DISCONTINUED`.

---

## 4. Method Signatures

```java
// RegisterProductService
public Product registerProduct(Product product);

// GetProductService
public Product getProduct(String productId);

// PublishProductService
public void publishProduct(String productId);

// SuspendProductService
public void suspendProduct(String productId);

// DiscontinueProductService
public void discontinueProduct(String productId);

// UpdateProductPriceService
public void updatePrice(String productId, Money newPrice);

// AddProductVariantService
public void addVariant(String productId, ProductVariant variant);
```
