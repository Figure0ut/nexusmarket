package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Address;

import java.util.List;

public class WarehouseManagementService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public WarehouseManagementService(WarehouseRepositoryPort warehouseRepositoryPort) {
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    public Warehouse registerWarehouse(Warehouse warehouse, User adminUser) {
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse cannot be null.");
        }
        if (adminUser == null) {
            throw new IllegalArgumentException("Admin user cannot be null.");
        }
        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new InvalidDomainStateException("Warehouse registration failure: Only ADMIN users can register warehouses.");
        }
        if (warehouseRepositoryPort.findById(warehouse.getWarehouseId()).isPresent()) {
            throw new DomainException("Warehouse with ID '" + warehouse.getWarehouseId() + "' already exists.");
        }

        return warehouseRepositoryPort.save(warehouse);
    }

    public Warehouse getWarehouse(String warehouseId) {
        if (warehouseId == null || warehouseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        return warehouseRepositoryPort.findById(warehouseId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + warehouseId));
    }

    public void activateWarehouse(String warehouseId) {
        Warehouse warehouse = getWarehouse(warehouseId);
        warehouse.activate();
        warehouseRepositoryPort.save(warehouse);
    }

    public void deactivateWarehouse(String warehouseId) {
        Warehouse warehouse = getWarehouse(warehouseId);
        warehouse.deactivate();
        warehouseRepositoryPort.save(warehouse);
    }

    public void updateWarehouseLocation(String warehouseId, Address newLocation) {
        Warehouse warehouse = getWarehouse(warehouseId);
        warehouse.updateLocation(newLocation);
        warehouseRepositoryPort.save(warehouse);
    }

    public List<Warehouse> getWarehousesByOwner(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner ID cannot be null or empty.");
        }
        return warehouseRepositoryPort.findByOwnerId(ownerId.trim());
    }
}
