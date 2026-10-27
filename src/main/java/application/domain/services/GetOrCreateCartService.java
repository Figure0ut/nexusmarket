package application.domain.services;

import application.domain.models.Cart;
import application.domain.ports.out.OrderRepositoryPort;

/**
 * Atomic domain service responsible exclusively for retrieving or initializing a buyer's cart.
 */
public class GetOrCreateCartService {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrCreateCartService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public Cart getOrCreateCart(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        return orderRepositoryPort.findCartByBuyerId(buyerId.trim())
                .orElseGet(() -> {
                    Cart newCart = new Cart("CART-" + buyerId.trim(), buyerId.trim());
                    return orderRepositoryPort.saveCart(newCart);
                });
    }
}
