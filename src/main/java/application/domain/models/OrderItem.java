package application.domain.models;

import application.domain.enums.ProductType;
import application.domain.valueobjects.Money;

import java.util.Objects;

/**
 * Line of an order. Physical items keep the warehouse where their stock was reserved,
 * so the sale outflow is registered in that same warehouse when the order is dispatched.
 * Digital items have no warehouse (they do not require inventory).
 */
public class OrderItem {

    private String productId;
    private String productName;
    private ProductType productType;
    private String warehouseId;
    private Money unitPrice;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(String productId, String productName, ProductType productType, String warehouseId,
                     Money unitPrice, int quantity) {
        setProductId(productId);
        setProductName(productName);
        setProductType(productType);
        setWarehouseId(warehouseId);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        this.productName = productName.trim();
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

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = (warehouseId == null || warehouseId.trim().isEmpty()) ? null : warehouseId.trim();
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null.");
        }
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public boolean isPhysical() {
        return productType == ProductType.PHYSICAL;
    }

    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
