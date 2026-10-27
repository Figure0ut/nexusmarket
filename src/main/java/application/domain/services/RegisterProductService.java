package application.domain.services;

import application.domain.exceptions.DomainException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

/**
 * Atomic domain service responsible exclusively for registering a new product in the catalog,
 * enforcing ID and SKU uniqueness invariants.
 */
public class RegisterProductService {

    private final ProductRepositoryPort productRepositoryPort;

    public RegisterProductService(ProductRepositoryPort productRepositoryPort) {
        if (productRepositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.productRepositoryPort = productRepositoryPort;
    }

    public Product registerProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (productRepositoryPort.findById(product.getProductId()).isPresent()) {
            throw new DomainException("Product with ID '" + product.getProductId() + "' already exists.");
        }
        if (productRepositoryPort.findBySku(product.getSku()).isPresent()) {
            throw new DomainException("Product with SKU '" + product.getSku().getCode() + "' already exists.");
        }

        return productRepositoryPort.save(product);
    }
}
