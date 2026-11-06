package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Cart;
import application.domain.models.Product;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;

/**
 * Atomic domain service responsible exclusively for adding a published product to a buyer's cart
 * (Domain 7, stage "Cart": provisional selection of products).
 * The unit price is always taken from the catalog, never from the client.
 */
public class AddItemToCartService {

    private final OrderRepositoryPort orderRepositoryPort;
    private final GetOrCreateCartService getOrCreateCartService;
    private final ProductRepositoryPort productRepositoryPort;

    public AddItemToCartService(OrderRepositoryPort orderRepositoryPort,
                                GetOrCreateCartService getOrCreateCartService,
                                ProductRepositoryPort productRepositoryPort) {
        if (orderRepositoryPort == null) {
            throw new IllegalArgumentException("OrderRepositoryPort cannot be null.");
        }
        if (getOrCreateCartService == null) {
            throw new IllegalArgumentException("GetOrCreateCartService cannot be null.");
        }
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.orderRepositoryPort = orderRepositoryPort;
        this.getOrCreateCartService = getOrCreateCartService;
        this.productRepositoryPort = productRepositoryPort;
    }

    public Cart addItemToCart(String buyerId, String productId, int quantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be greater than zero.");
        }
        Product product = productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        if (!product.isPublished()) {
            throw new InvalidDomainStateException("Product '" + productId + "' is not PUBLISHED (status: " + product.getStatus() + ") and cannot be purchased.");
        }
        Cart cart = getOrCreateCartService.getOrCreateCart(buyerId);
        cart.addItem(product.getProductId(), product.getPrice(), quantity);
        return orderRepositoryPort.saveCart(cart);
    }
}
