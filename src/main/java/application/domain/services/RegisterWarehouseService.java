package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Atomic domain service responsible exclusively for registering a warehouse under ADMIN governance.
 */
public class RegisterWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public RegisterWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort) {
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
}
