package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

/**
 * Atomic domain service responsible exclusively for permanently discontinuing a product.
 */
public class DiscontinueProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public DiscontinueProductService(ProductRepositoryPort productRepositoryPort) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.productRepositoryPort = productRepositoryPort;
    }

    public void discontinueProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        Product product = productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        product.discontinue();
        productRepositoryPort.save(product);
    }
}
