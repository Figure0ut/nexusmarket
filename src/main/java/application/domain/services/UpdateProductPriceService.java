package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.Money;

/**
 * Atomic domain service responsible exclusively for updating a product's price.
 */
public class UpdateProductPriceService {

    private final ProductRepositoryPort productRepositoryPort;

    public UpdateProductPriceService(ProductRepositoryPort productRepositoryPort) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.productRepositoryPort = productRepositoryPort;
    }

    public void updatePrice(String productId, Money newPrice) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        Product product = productRepositoryPort.findById(productId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        product.updatePrice(newPrice);
        productRepositoryPort.save(product);
    }
}
