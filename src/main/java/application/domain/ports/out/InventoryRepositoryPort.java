package application.domain.ports.out;

import application.domain.models.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId);
    List<Inventory> findByProductId(String productId);
    Inventory save(Inventory inventory);
}
