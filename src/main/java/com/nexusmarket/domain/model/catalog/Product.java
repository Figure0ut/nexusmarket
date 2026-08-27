package com.nexusmarket.domain.model.catalog;

import com.nexusmarket.domain.model.common.Money;
import com.nexusmarket.domain.model.common.SKU;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Pure Java Domain Aggregate Root representing a Product in NexusMarket catalog.
 */
public class Product {

    private final String productId;
    private final SKU sku;
    private final String sellerId;
    private String name;
    private String description;
    private Money price;
    private final ProductType productType;
    private ProductStatus status;
    private final List<ProductVariant> variants;

    public Product(String productId, SKU sku, String sellerId, String name, String description,
                   Money price, ProductType productType, ProductStatus status, List<ProductVariant> variants) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (sku == null) {
            throw new IllegalArgumentException("Product SKU cannot be null.");
        }
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        if (price == null || price.isZero()) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        if (productType == null) {
            throw new IllegalArgumentException("Product type cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Product status cannot be null.");
        }

        this.productId = productId.trim();
        this.sku = sku;
        this.sellerId = sellerId.trim();
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.price = price;
        this.productType = productType;
        this.status = status;
        this.variants = new ArrayList<>();

        if (variants != null) {
            for (ProductVariant v : variants) {
                if (v != null && !this.variants.contains(v)) {
                    this.variants.add(v);
                }
            }
        }
    }

    public Product(String productId, String skuCode, String sellerId, String name, double priceValue, ProductType productType) {
        this(productId, new SKU(skuCode), sellerId, name, "", new Money(priceValue), productType, ProductStatus.DRAFT, new ArrayList<>());
    }

    public void publish() {
        if (status == ProductStatus.DISCONTINUED) {
            throw new IllegalStateException("Cannot publish a discontinued product.");
        }
        this.status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        if (status != ProductStatus.PUBLISHED) {
            throw new IllegalStateException("Only published products can be suspended.");
        }
        this.status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public void updatePrice(Money newPrice) {
        if (newPrice == null || newPrice.isZero()) {
            throw new IllegalArgumentException("New price must be greater than zero.");
        }
        this.price = newPrice;
    }

    public void addVariant(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("Variant cannot be null.");
        }
        if (!this.variants.contains(variant)) {
            this.variants.add(variant);
        }
    }

    public String getProductId() {
        return productId;
    }

    public SKU getSku() {
        return sku;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public ProductType getProductType() {
        return productType;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
