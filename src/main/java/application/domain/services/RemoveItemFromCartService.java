package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Cart;
import application.domain.ports.out.OrderRepositoryPort;

/**
 * Atomic domain service responsible exclusively for removing an item from a buyer's cart.
 */
public class RemoveItemFromCartService {

    private final OrderRepositoryPort orderRepositoryPort;

    public RemoveItemFromCartService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public Cart removeItemFromCart(String buyerId, String productId) {
        if (buyerId == null || buyerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buyer ID cannot be null or empty.");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }

        Cart cart = orderRepositoryPort.findCartByBuyerId(buyerId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for buyer: " + buyerId));

        cart.removeItem(productId.trim());
        return orderRepositoryPort.saveCart(cart);
    }
}
