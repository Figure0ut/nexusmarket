package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.WarehouseType;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;

/**
 * Atomic domain service responsible exclusively for the incorporation of sellers
 * (section 6.1, step 1: "The Administrator registers the seller and its first warehouse").
 * Sellers cannot self-register (Domain 3).
 */
public class IncorporateSellerService {

    private final RegisterUserService registerUserService;
    private final RegisterWarehouseService registerWarehouseService;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public IncorporateSellerService(RegisterUserService registerUserService,
                                    RegisterWarehouseService registerWarehouseService,
                                    WarehouseRepositoryPort warehouseRepositoryPort) {
        if (registerUserService == null) {
            throw new IllegalArgumentException("RegisterUserService cannot be null.");
        }
        if (registerWarehouseService == null) {
            throw new IllegalArgumentException("RegisterWarehouseService cannot be null.");
        }
        this.registerUserService = registerUserService;
        if (warehouseRepositoryPort == null) {
            throw new IllegalArgumentException("WarehouseRepositoryPort cannot be null.");
        }
        this.registerWarehouseService = registerWarehouseService;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    /**
     * Registers and incorporates a seller together with its first warehouse.
     *
     * @param seller         seller to incorporate
     * @param firstWarehouse first warehouse of the seller (must be of type SELLER)
     * @param adminUser      the Administrator performing the operation
     * @return the incorporated (ACTIVE) seller
     */
    public Seller incorporateSeller(Seller seller, Warehouse firstWarehouse, User adminUser) {
        if (seller == null) {
            throw new IllegalArgumentException("Seller cannot be null.");
        }
        if (firstWarehouse == null) {
            throw new IllegalArgumentException("The first warehouse of the seller is mandatory.");
        }
        if (adminUser == null || adminUser.getRole() != UserRole.ADMIN) {
            throw new InvalidDomainStateException("Seller incorporation failure: only an ADMIN can incorporate sellers.");
        }
        if (firstWarehouse.getType() != WarehouseType.SELLER || !seller.getIdentifier().equals(firstWarehouse.getOwnerId())) {
            throw new InvalidDomainStateException("The first warehouse must be a SELLER warehouse owned by seller '" + seller.getIdentifier() + "'.");
        }
        if (warehouseRepositoryPort.findById(firstWarehouse.getWarehouseId()).isPresent()) {
            throw new DomainException("Warehouse with ID '" + firstWarehouse.getWarehouseId() + "' already exists.");
        }
        seller.incorporate(adminUser);
        Seller registered = (Seller) registerUserService.registerUser(seller);
        registerWarehouseService.registerWarehouse(firstWarehouse, adminUser);
        return registered;
    }
}
