package com.nexusmarket.domain.model.order;

import com.nexusmarket.domain.model.common.Money;

import java.util.Objects;

/**
 * Entity representing an immutable line item inside a finalized or pending Order.
 */
public class OrderItem {

    private final String productId;
    private final String productName;
    private final Money unitPrice;
    private final int quantity;

    public OrderItem(String productId, String productName, Money unitPrice, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Item quantity must be greater than zero.");
        }

        this.productId = productId.trim();
        this.productName = productName.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
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
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
