package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.ProductVariant;

/**
 * Atomic domain service responsible exclusively for adding a product variant to an existing product.
 */
public class AddProductVariantService {

    private final ProductRepositoryPort productRepositoryPort;

    public AddProductVariantService(ProductRepositoryPort productRepositoryPort) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.productRepositoryPort = productRepositoryPort;
    }

    public void addVariant(String productId, ProductVariant variant) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        Product product = productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        product.addVariant(variant);
        productRepositoryPort.save(product);
    }
}
