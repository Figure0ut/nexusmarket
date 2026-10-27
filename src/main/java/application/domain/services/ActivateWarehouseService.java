package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Atomic domain service responsible exclusively for activating a warehouse.
 */
public class ActivateWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public ActivateWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public void activateWarehouse(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
        warehouse.activate();
        warehouseRepositoryPort.save(warehouse);
    }
}
