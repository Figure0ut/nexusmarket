package application.infrastructure.adapters.out.memory;

import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final Map<String, Inventory> storage = new ConcurrentHashMap<>();

    private String key(String productId, String warehouseId) {
        return productId + "_" + warehouseId;
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId) {
        return Optional.ofNullable(storage.get(key(productId, warehouseId)));
    }

    @Override
    public List<Inventory> findByProductId(String productId) {
        return storage.values().stream()
                .filter(i -> i.getProductId().equals(productId))
                .toList();
    }

    @Override
    public Inventory save(Inventory inventory) {
        storage.put(key(inventory.getProductId(), inventory.getWarehouseId()), inventory);
        return inventory;
    }
}
