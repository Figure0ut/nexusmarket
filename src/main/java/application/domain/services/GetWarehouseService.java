package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying a warehouse by its identifier.
 */
public class GetWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public GetWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public Warehouse getWarehouse(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        return warehouseRepositoryPort.findById(warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
    }
}
