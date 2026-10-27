package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;

public class ProductRequestDto {
    private String productId;
    private String sku;
    private String sellerId;
    private String name;
    private String description;
    private double price;
    private ProductType type;
    private ProductStatus status;

    public ProductRequestDto() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}
