package application.domain.models;

import application.domain.valueobjects.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Cart {

    private String cartId;
    private String buyerId;
    private List<CartItem> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(String cartId, String buyerId, List<CartItem> items) {
        setCartId(cartId);
        setBuyerId(buyerId);
        setItems(items);
    }

    public Cart(String cartId, String buyerId) {
        this(cartId, buyerId, new ArrayList<>());
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new IllegalArgumentException("Cart ID cannot be null or empty.");
        }
        this.cartId = cartId.trim();
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        this.buyerId = buyerId.trim();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<CartItem> items) {
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
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
