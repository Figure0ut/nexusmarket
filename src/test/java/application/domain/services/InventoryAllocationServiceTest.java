package application.domain.services;

import application.domain.enums.WarehouseType;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Inventory;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
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

class InventoryAllocationServiceTest {

    private InventoryAllocationService allocationService;
    private MockInventoryRepository inventoryRepository;
    private MockWarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        inventoryRepository = new MockInventoryRepository();
        warehouseRepository = new MockWarehouseRepository();
        allocationService = new InventoryAllocationService(inventoryRepository, warehouseRepository);
    }

    @Test
    @DisplayName("Should successfully add stock, reserve, and confirm sale")
    void shouldManageStockOperations() {
        Warehouse warehouse = new Warehouse("WH-1", "Central WH", new Address("St", "City", "ST", "1", "USA"), WarehouseType.MARKETPLACE, "ADM-1", true);
        warehouseRepository.save(warehouse);

        allocationService.addStock("INV-1", "P-1", "WH-1", 50);
        assertEquals(50, allocationService.getTotalAvailableStock("P-1"));

        allocationService.reserveStock("P-1", "WH-1", 20);
        Inventory inv = allocationService.getInventory("P-1", "WH-1");
        assertEquals(30, inv.getAvailableStock().getValue());
        assertEquals(20, inv.getReservedStock().getValue());

        allocationService.confirmSale("P-1", "WH-1", 20);
        inv = allocationService.getInventory("P-1", "WH-1");
        assertEquals(30, inv.getAvailableStock().getValue());
        assertEquals(0, inv.getReservedStock().getValue());

        allocationService.markStockAsDamaged("P-1", "WH-1", 5);
        inv = allocationService.getInventory("P-1", "WH-1");
        assertEquals(25, inv.getAvailableStock().getValue());
        assertEquals(5, inv.getDamagedStock().getValue());
    }

    @Test
    @DisplayName("Should reject adding stock to inactive warehouse")
    void shouldRejectInactiveWarehouse() {
        Warehouse warehouse = new Warehouse("WH-1", "Central WH", new Address("St", "City", "ST", "1", "USA"), WarehouseType.MARKETPLACE, "ADM-1", false);
        warehouseRepository.save(warehouse);

        assertThrows(InvalidDomainStateException.class, () ->
                allocationService.addStock("INV-1", "P-1", "WH-1", 50)
        );
    }

    private static class MockInventoryRepository implements InventoryRepositoryPort {
        private final Map<String, Inventory> storage = new HashMap<>();

        @Override public Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId) {
            return Optional.ofNullable(storage.get(productId + "_" + warehouseId));
        }

        @Override public List<Inventory> findByProductId(String productId) {
            return storage.values().stream().filter(i -> i.getProductId().equals(productId)).toList();
        }

        @Override public Inventory save(Inventory inventory) {
            storage.put(inventory.getProductId() + "_" + inventory.getWarehouseId(), inventory);
            return inventory;
        }
    }

    private static class MockWarehouseRepository implements WarehouseRepositoryPort {
        private final Map<String, Warehouse> storage = new HashMap<>();

        @Override public Optional<Warehouse> findById(String warehouseId) { return Optional.ofNullable(storage.get(warehouseId)); }
        @Override public List<Warehouse> findByOwnerId(String ownerId) { return List.of(); }
        @Override public Warehouse save(Warehouse warehouse) { storage.put(warehouse.getWarehouseId(), warehouse); return warehouse; }
    }
}
