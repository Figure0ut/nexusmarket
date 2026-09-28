package application.domain.ports.out;

import application.domain.models.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepositoryPort {
    Optional<Warehouse> findById(String warehouseId);
    List<Warehouse> findByOwnerId(String ownerId);
    Warehouse save(Warehouse warehouse);
}
