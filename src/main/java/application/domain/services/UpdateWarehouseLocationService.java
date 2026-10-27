package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Address;

/**
 * Atomic domain service responsible exclusively for updating a warehouse physical location.
 */
public class UpdateWarehouseLocationService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public UpdateWarehouseLocationService(WarehouseRepositoryPort warehouseRepositoryPort) {
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public void updateWarehouseLocation(String warehouseId, Address newLocation) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        if (newLocation == null) {
            throw new IllegalArgumentException("New location address cannot be null.");
        }
        Warehouse warehouse = warehouseRepositoryPort.findById(warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
        warehouse.updateLocation(newLocation);
        warehouseRepositoryPort.save(warehouse);
    }
}
