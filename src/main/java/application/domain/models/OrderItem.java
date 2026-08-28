package application.domain.models;

import application.domain.valueobjects.Money;

import java.util.Objects;

public class OrderItem {

    private String productId;
    private String productName;
    private Money unitPrice;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(String productId, String productName, Money unitPrice, int quantity) {
        setProductId(productId);
        setProductName(productName);
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
