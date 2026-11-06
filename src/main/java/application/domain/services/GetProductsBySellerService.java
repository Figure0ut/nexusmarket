package application.domain.services;

import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for querying the products registered by a seller (any status).
 */
public class GetProductsBySellerService {

    private final ProductRepositoryPort repositoryPort;

    public GetProductsBySellerService(ProductRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public List<Product> getProductsBySeller(String sellerId) {
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be null or empty.");
        }
        return repositoryPort.findBySellerId(sellerId.trim());
    }
}
