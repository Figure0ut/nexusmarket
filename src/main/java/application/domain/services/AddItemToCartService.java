package application.domain.services;

import application.domain.models.Cart;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.Money;

/**
 * Atomic domain service responsible exclusively for adding an item with quantity to a buyer's cart.
 */
public class AddItemToCartService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final GetOrCreateCartService getOrCreateCartService;

    public AddItemToCartService(OrderRepositoryPort orderRepositoryPort, GetOrCreateCartService getOrCreateCartService) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (getOrCreateCartService == null) {
            throw new IllegalArgumentException("GetOrCreateCartService cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
        this.getOrCreateCartService = getOrCreateCartService;
    }

    public Cart addItemToCart(String buyerId, String productId, Money unitPrice, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be greater than zero.");
        }

        Cart cart = getOrCreateCartService.getOrCreateCart(buyerId);
        cart.addItem(productId.trim(), unitPrice, quantity);
        return orderRepositoryPort.saveCart(cart);
    }
}
