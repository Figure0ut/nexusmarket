package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

/**
 * Atomic domain service responsible exclusively for suspending an active product.
 */
public class SuspendProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public SuspendProductService(ProductRepositoryPort productRepositoryPort) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.productRepositoryPort = productRepositoryPort;
    }

    public void suspendProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        Product product = productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        product.suspend();
        productRepositoryPort.save(product);
    }
}
