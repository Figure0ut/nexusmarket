package application.infrastructure.adapters.out.mongo.document;

import application.domain.enums.ProductStatus;
import application.domain.enums.ProductType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
public class ProductDocument {

    @Id
    private String productId;

    @Indexed(unique = true)
    private String sku;

    private String sellerId;
    private String name;
    private String description;
    private double price;
    private ProductType productType;
    private ProductStatus status;
    private List<ProductVariantDocument> variants = new ArrayList<>();

    public static class ProductVariantDocument {
        private String name;
        private String value;

        public ProductVariantDocument() {}
        public ProductVariantDocument(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public ProductDocument() {}

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

    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public List<ProductVariantDocument> getVariants() { return variants; }
    public void setVariants(List<ProductVariantDocument> variants) { this.variants = variants; }
}
