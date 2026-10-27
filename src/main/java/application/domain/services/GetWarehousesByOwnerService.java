package application.domain.services;

import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

import java.util.List;

/**
 * Atomic domain service responsible exclusively for querying warehouses owned by a merchant/entity.
 */
public class GetWarehousesByOwnerService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public GetWarehousesByOwnerService(WarehouseRepositoryPort warehouseRepositoryPort) {
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public List<Warehouse> getWarehousesByOwner(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner ID cannot be null or empty.");
        }
        return warehouseRepositoryPort.findByOwnerId(ownerId.trim());
    }
}
