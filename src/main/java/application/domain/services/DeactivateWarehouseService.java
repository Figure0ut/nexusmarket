package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Atomic domain service responsible exclusively for deactivating a warehouse.
 */
public class DeactivateWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public DeactivateWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public void deactivateWarehouse(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
        warehouse.deactivate();
        warehouseRepositoryPort.save(warehouse);
    }
}
