package com.nexusmarket.domain.model.order;

import com.nexusmarket.domain.model.common.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Pure Java Aggregate Root representing a buyer's Shopping Cart.
 */
public class Cart {

    private final String cartId;
    private final String buyerId;
    private final List<CartItem> items;

    public Cart(String cartId, String buyerId, List<CartItem> items) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new IllegalArgumentException("Cart ID cannot be null or empty.");
        }
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }

        this.cartId = cartId.trim();
        this.buyerId = buyerId.trim();
        this.items = new ArrayList<>();

        if (items != null) {
            this.items.addAll(items);
        }
    }

    public Cart(String cartId, String buyerId) {
        this(cartId, buyerId, new ArrayList<>());
    }

    public void addItem(String productId, Money unitPrice, int quantity) {
        Optional<CartItem> existing = items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.updateQuantity(item.getQuantity() + quantity);
        } else {
            items.add(new CartItem(productId, unitPrice, quantity));
        }
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public Money calculateTotal() {
        Money total = Money.zero();
        for (CartItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    public String getCartId() {
        return cartId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(cartId, cart.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }
}
