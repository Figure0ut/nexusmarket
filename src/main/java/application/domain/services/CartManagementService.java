package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Cart;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.Money;

/**
 * Domain Service governing shopping cart operations prior to checkout.
 * Coordinates adding, updating, and removing items in buyer shopping carts,
 * ensuring carts exist and are persisted via {@link OrderRepositoryPort}.
 */
public class CartManagementService {

    private final OrderRepositoryPort orderRepositoryPort;

    public CartManagementService(OrderRepositoryPort orderRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
    }

    /**
     * Retrieves the shopping cart for a given buyer, creating an empty cart if one does not exist.
     *
     * @param buyerId the buyer's unique identifier
     * @return the existing or newly created Cart
     */
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

    /**
     * Adds an item to the buyer's cart, creating the cart if it does not exist.
     * If the item already exists in the cart, its quantity is incremented.
     *
     * @param buyerId   the buyer's identifier
     * @param productId the product identifier
     * @param unitPrice the unit price of the product
     * @param quantity  the quantity to add (must be > 0)
     * @return the updated Cart
     */
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

        Cart cart = getOrCreateCart(buyerId);
        cart.addItem(productId.trim(), unitPrice, quantity);
        return orderRepositoryPort.saveCart(cart);
    }

    /**
     * Removes an item from the buyer's cart.
     *
     * @param buyerId   the buyer's identifier
     * @param productId the product identifier to remove
     * @return the updated Cart
     */
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

    /**
     * Clears all items from the buyer's cart.
     *
     * @param buyerId the buyer's identifier
     */
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
