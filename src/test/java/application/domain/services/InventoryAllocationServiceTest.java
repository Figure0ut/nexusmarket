package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryAllocationServiceTest {

    private final MarketplaceFixture f = new MarketplaceFixture().withCatalog();

    @Test
    @DisplayName("Inventory is linked to an existing product and an existing warehouse")
    void inventoryNeedsProductAndWarehouse() {
        assertThrows(EntityNotFoundException.class, () -> f.addStock.addStock("P-404", "WH-SEL-1", 1));
        assertThrows(EntityNotFoundException.class, () -> f.addStock.addStock("P-LAPTOP", "WH-404", 1));
        assertEquals(15, f.addStock.addStock("P-LAPTOP", "WH-SEL-1", 5).getAvailableStock().getValue());
    }

    @Test
    @DisplayName("Digital products do not have inventory")
    void digitalProductsHaveNoInventory() {
        assertThrows(InvalidDomainStateException.class, () -> f.addStock.addStock("P-EBOOK", "WH-SEL-1", 5));
    }

    @Test
    @DisplayName("Stock cannot be added to an inactive warehouse")
    void inactiveWarehouse() {
        new DeactivateWarehouseService(f.warehouses).deactivateWarehouse("WH-SEL-1");
        assertThrows(InvalidDomainStateException.class, () -> f.addStock.addStock("P-LAPTOP", "WH-SEL-1", 5));
    }
}
