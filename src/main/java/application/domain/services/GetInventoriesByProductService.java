package application.domain.services;

import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for querying the distributed inventory of a product
 * across all warehouses (Domain 6).
 */
public class GetInventoriesByProductService {

    private final InventoryRepositoryPort repositoryPort;

    public GetInventoriesByProductService(InventoryRepositoryPort repositoryPort) {
        if (repositoryPort == null) {
            throw new IllegalArgumentException("InventoryRepositoryPort cannot be null.");
        }
        this.repositoryPort = repositoryPort;
    }

    public List<Inventory> getInventoriesByProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        return repositoryPort.findByProductId(productId.trim());
    }
}
