package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Cart;
import application.domain.ports.out.OrderRepositoryPort;

/**
 * Atomic domain service responsible exclusively for clearing all items from a buyer's cart.
 */
public class ClearCartService {

    private final OrderRepositoryPort orderRepositoryPort;

    public ClearCartService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public void clearCart(String buyerId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }

        Cart cart = orderRepositoryPort.findCartByBuyerId(buyerId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for buyer: " + buyerId));

        cart.clear();
        orderRepositoryPort.saveCart(cart);
    }
}
