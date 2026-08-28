package application.domain.models;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;
import application.domain.valueobjects.SKU;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Product {

    private String productId;
    private SKU sku;
    private String sellerId;
    private String name;
    private String description;
    private Money price;
    private ProductType productType;
    private ProductStatus status;
    private List<ProductVariant> variants = new ArrayList<>();

    public Product() {
    }

    public Product(String productId, SKU sku, String sellerId, String name, String description,
                   Money price, ProductType productType, ProductStatus status, List<ProductVariant> variants) {
        setProductId(productId);
        setSku(sku);
        setSellerId(sellerId);
        setName(name);
        setDescription(description);
        setPrice(price);
        setProductType(productType);
        setStatus(status);
        setVariants(variants);
    }

    public Product(String productId, String skuCode, String sellerId, String name, double priceValue, ProductType productType) {
        this(productId, new SKU(skuCode), sellerId, name, "", new Money(priceValue), productType, ProductStatus.DRAFT, new ArrayList<>());
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        this.productId = productId.trim();
    }

    public SKU getSku() {
        return sku;
    }

    public void setSku(SKU sku) {
        if (sku == null) {
            throw new IllegalArgumentException("Product SKU cannot be null.");
        }
        this.sku = sku;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be null or empty.");
        }
        this.sellerId = sellerId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        if (price == null || price.isZero()) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        this.price = price;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        if (productType == null) {
            throw new IllegalArgumentException("Product type cannot be null.");
        }
        this.productType = productType;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Product status cannot be null.");
        }
        this.status = status;
    }

    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = new ArrayList<>();
        if (variants != null) {
            for (ProductVariant v : variants) {
                if (v != null && !this.variants.contains(v)) {
                    this.variants.add(v);
                }
            }
        }
    }

    public void publish() {
        if (status == ProductStatus.DISCONTINUED) {
            throw new IllegalStateException("Cannot publish a discontinued product.");
        }
        setStatus(ProductStatus.PUBLISHED);
    }

    public void suspend() {
        if (status != ProductStatus.PUBLISHED) {
            throw new IllegalStateException("Only published products can be suspended.");
        }
        setStatus(ProductStatus.SUSPENDED);
    }

    public void discontinue() {
        setStatus(ProductStatus.DISCONTINUED);
    }

    public void updatePrice(Money newPrice) {
        setPrice(newPrice);
    }

    public void addVariant(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("Variant cannot be null.");
        }
        if (!this.variants.contains(variant)) {
            this.variants.add(variant);
        }
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
