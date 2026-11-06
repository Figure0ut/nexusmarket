package application.infrastructure.adapters.out.memory;

import application.domain.models.Inventory;
import application.domain.ports.out.InventoryRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory test double of the output port (used only by unit tests).
 */
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

    @Override
    public List<Inventory> findByWarehouseId(String warehouseId) {
        return storage.values().stream().filter(i -> i.getWarehouseId().equals(warehouseId)).toList();
    }

    @Override
    public List<Inventory> findAll() {
        return List.copyOf(storage.values());
    }
}
