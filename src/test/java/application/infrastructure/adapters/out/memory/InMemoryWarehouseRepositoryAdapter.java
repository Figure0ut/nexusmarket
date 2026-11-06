package application.infrastructure.adapters.out.memory;

import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory test double of the output port (used only by unit tests).
 */
public class InMemoryWarehouseRepositoryAdapter implements WarehouseRepositoryPort {

    private final Map<String, Warehouse> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Warehouse> findById(String warehouseId) {
        return Optional.ofNullable(storage.get(warehouseId));
    }

    @Override
    public List<Warehouse> findByOwnerId(String ownerId) {
        return storage.values().stream()
                .filter(w -> w.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        storage.put(warehouse.getWarehouseId(), warehouse);
        return warehouse;
    }

    @Override
    public List<Warehouse> findAll() {
        return List.copyOf(storage.values());
    }
}
