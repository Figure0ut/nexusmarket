package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying a product by its identifier.
 */
public class GetProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public GetProductService(ProductRepositoryPort productRepositoryPort) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.productRepositoryPort = productRepositoryPort;
    }

    public Product getProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        return productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
    }
}
