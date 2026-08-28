package application.domain.models;

import application.domain.valueobjects.Money;

import java.util.Objects;

public class CartItem {

    private String productId;
    private Money unitPrice;
    private int quantity;

    public CartItem() {
    }

    public CartItem(String productId, Money unitPrice, int quantity) {
        setProductId(productId);
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

    public void updateQuantity(int newQuantity) {
        setQuantity(newQuantity);
    }

    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(productId, cartItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
