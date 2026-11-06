package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.enums.WarehouseType;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseManagementServiceTest {

    private final MarketplaceFixture f = new MarketplaceFixture();

    @Test
    @DisplayName("Marketplace warehouses are owned by the marketplace")
    void marketplaceWarehouse() {
        Warehouse w = f.registerWarehouse.registerWarehouse(
                new Warehouse("WH-M", "Central", new Address("Av 1"), WarehouseType.MARKETPLACE, "anything", true), f.admin);
        assertEquals(Warehouse.MARKETPLACE_OWNER_ID, w.getOwnerId());
    }

    @Test
    @DisplayName("Seller warehouses must belong to an existing seller")
    void sellerWarehouseNeedsSeller() {
        assertThrows(EntityNotFoundException.class, () -> f.registerWarehouse.registerWarehouse(
                new Warehouse("WH-X", "X", new Address("Av 1"), WarehouseType.SELLER, "SEL-404", true), f.admin));
    }

    @Test
    @DisplayName("Only the administrator registers warehouses")
    void onlyAdmin() {
        User buyer = new User("BUY-1", "CC-B", "Buyer", "b@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);
        assertThrows(InvalidDomainStateException.class, () -> f.registerWarehouse.registerWarehouse(
                new Warehouse("WH-M", "Central", new Address("Av 1"), WarehouseType.MARKETPLACE, null, true), buyer));
    }
}
