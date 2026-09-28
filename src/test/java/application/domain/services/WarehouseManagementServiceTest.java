package application.domain.services;

import application.domain.enums.WarehouseType;
import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseManagementServiceTest {

    private WarehouseManagementService warehouseService;
    private MockWarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        warehouseRepository = new MockWarehouseRepository();
        warehouseService = new WarehouseManagementService(warehouseRepository);
    }

    @Test
    @DisplayName("Should successfully register, activate and deactivate warehouse when authorized by ADMIN")
    void shouldRegisterAndManageWarehouse() {
        User admin = new User("ADM-1", "Admin", "admin@nexusmarket.com", UserRole.ADMIN, UserStatus.ACTIVE);
        Warehouse wh = new Warehouse("WH-1", "Central Hub", new Address("St 1", "City", "ST", "111", "USA"), WarehouseType.MARKETPLACE, "ADM-1", true);

        Warehouse saved = warehouseService.registerWarehouse(wh, admin);
        assertNotNull(saved);
        assertTrue(warehouseService.getWarehouse("WH-1").isActive());

        warehouseService.deactivateWarehouse("WH-1");
        assertFalse(warehouseService.getWarehouse("WH-1").isActive());

        warehouseService.activateWarehouse("WH-1");
        assertTrue(warehouseService.getWarehouse("WH-1").isActive());
    }

    @Test
    @DisplayName("Should reject warehouse registration by non-admin user")
    void shouldRejectNonAdminWarehouseRegistration() {
        User buyer = new User("BUY-1", "Buyer", "buyer@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);
        Warehouse wh = new Warehouse("WH-1", "Central Hub", new Address("St 1", "City", "ST", "111", "USA"), WarehouseType.MARKETPLACE, "ADM-1", true);

        assertThrows(InvalidDomainStateException.class, () ->
                warehouseService.registerWarehouse(wh, buyer)
        );
    }

    private static class MockWarehouseRepository implements WarehouseRepositoryPort {
        private final Map<String, Warehouse> storage = new HashMap<>();

        @Override public Optional<Warehouse> findById(String warehouseId) { return Optional.ofNullable(storage.get(warehouseId)); }
        @Override public List<Warehouse> findByOwnerId(String ownerId) {
            return storage.values().stream().filter(w -> w.getOwnerId().equals(ownerId)).toList();
        }
        @Override public Warehouse save(Warehouse warehouse) { storage.put(warehouse.getWarehouseId(), warehouse); return warehouse; }
    }
}
