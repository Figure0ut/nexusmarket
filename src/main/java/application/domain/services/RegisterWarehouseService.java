package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.WarehouseType;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Atomic domain service responsible exclusively for registering a warehouse under ADMIN governance.
 * Marketplace warehouses belong to the marketplace; Seller warehouses must belong to an existing seller
 * (or to the seller being incorporated in the same operation).
 */
public class RegisterWarehouseService {

    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public RegisterWarehouseService(WarehouseRepositoryPort warehouseRepositoryPort, UserRepositoryPort userRepositoryPort) {
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        if (userRepositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
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
        if (warehouse.getType() == WarehouseType.SELLER) {
            User owner = userRepositoryPort.findById(warehouse.getOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Seller not found with identifier: " + warehouse.getOwnerId()));
            if (owner.getRole() != UserRole.SELLER) {
                throw new InvalidDomainStateException("Owner '" + owner.getIdentifier() + "' of a SELLER warehouse must have the SELLER role.");
            }
        }
        return warehouseRepositoryPort.save(warehouse);
    }
}
