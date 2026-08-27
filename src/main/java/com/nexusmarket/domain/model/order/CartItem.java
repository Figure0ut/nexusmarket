package com.nexusmarket.domain.model.order;

import com.nexusmarket.domain.model.common.Money;

import java.util.Objects;

/**
 * Entity representing an item inside a shopping cart.
 */
public class CartItem {

    private final String productId;
    private final Money unitPrice;
    private int quantity;

    public CartItem(String productId, Money unitPrice, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be greater than zero.");
        }

        this.productId = productId.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("New quantity must be greater than zero.");
        }
        this.quantity = newQuantity;
    }

    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }

    public String getProductId() {
        return productId;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
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
