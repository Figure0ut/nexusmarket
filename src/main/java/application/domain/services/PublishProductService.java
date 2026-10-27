package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

/**
 * Atomic domain service responsible exclusively for transitioning a product to PUBLISHED.
 */
public class PublishProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public PublishProductService(ProductRepositoryPort productRepositoryPort) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.productRepositoryPort = productRepositoryPort;
    }

    public void publishProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        Product product = productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        product.publish();
        productRepositoryPort.save(product);
    }
}
