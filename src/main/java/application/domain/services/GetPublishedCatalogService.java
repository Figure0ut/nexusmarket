package application.domain.services;

import application.domain.models.Product;
import application.domain.ports.out.ProductRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for querying the public catalog:
 * only PUBLISHED products are visible (section 6.1, step 4).
 */
public class GetPublishedCatalogService {

    private final ProductRepositoryPort repositoryPort;

    public GetPublishedCatalogService(ProductRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("ProductRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public List<Product> getPublishedCatalog() {
        return repositoryPort.findAll().stream().filter(Product::isPublished).toList();
    }
}
