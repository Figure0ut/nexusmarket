package application.infrastructure.adapters.in.rest.dto;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import application.domain.models.Product;
import application.domain.valueobjects.ProductVariant;

import java.util.ArrayList;
import java.util.List;

public class ProductResponseDto {
    private String productId;
    private String sku;
    private String sellerId;
    private String name;
    private String description;
    private double price;
    private ProductType type;
    private ProductStatus status;
    private List<ProductVariantDto> variants = new ArrayList<>();

    public static class ProductVariantDto {
        private String name;
        private String value;

        public ProductVariantDto() {}
        public ProductVariantDto(String name, String value) {
            this.name = name;
            this.value = value;
        }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public ProductResponseDto() {}

    public static ProductResponseDto fromDomain(Product product) {
        if (product == null) return null;
        ProductResponseDto dto = new ProductResponseDto();
        dto.setProductId(product.getProductId());
        dto.setSku(product.getSku() != null ? product.getSku().getCode() : null);
        dto.setSellerId(product.getSellerId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice() != null ? product.getPrice().getAmount().doubleValue() : 0.0);
        dto.setType(product.getProductType());
        dto.setStatus(product.getStatus());

        if (product.getVariants() != null) {
            for (ProductVariant v : product.getVariants()) {
                dto.getVariants().add(new ProductVariantDto(v.getName(), v.getValue()));
            }
        }
        return dto;
    }

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

    public List<ProductVariantDto> getVariants() { return variants; }
    public void setVariants(List<ProductVariantDto> variants) { this.variants = variants; }
}
