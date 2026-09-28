package application.domain.services;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductVariant;

public class CatalogManagementService {

    private final ProductRepositoryPort productRepositoryPort;

    public CatalogManagementService(ProductRepositoryPort productRepositoryPort) {
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

    public Product getProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        return productRepositoryPort.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
    }

    public void publishProduct(String productId) {
        Product product = getProduct(productId);
        product.publish();
        productRepositoryPort.save(product);
    }

    public void suspendProduct(String productId) {
        Product product = getProduct(productId);
        product.suspend();
        productRepositoryPort.save(product);
    }

    public void discontinueProduct(String productId) {
        Product product = getProduct(productId);
        product.discontinue();
        productRepositoryPort.save(product);
    }

    public void updatePrice(String productId, Money newPrice) {
        Product product = getProduct(productId);
        product.updatePrice(newPrice);
        productRepositoryPort.save(product);
    }

    public void addVariant(String productId, ProductVariant variant) {
        Product product = getProduct(productId);
        product.addVariant(variant);
        productRepositoryPort.save(product);
    }
}
