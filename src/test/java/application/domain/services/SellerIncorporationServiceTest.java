package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.enums.WarehouseType;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SellerIncorporationServiceTest {

    private final MarketplaceFixture f = new MarketplaceFixture();

    private Seller newSeller() {
        return new Seller("SEL-9", "NIT-9", "Seller Nine", new Email("nine@nexusmarket.com"), UserStatus.PENDING_INCORPORATION, null, null);
    }

    private Warehouse firstWarehouse() {
        return new Warehouse("WH-9", "Nine Warehouse", new Address("Street 9"), WarehouseType.SELLER, "SEL-9", true);
    }

    @Test
    @DisplayName("The administrator registers the seller together with its first warehouse")
    void adminIncorporatesSellerWithFirstWarehouse() {
        Seller seller = f.incorporateSeller.incorporateSeller(newSeller(), firstWarehouse(), f.admin);
        assertEquals(UserStatus.ACTIVE, seller.getStatus());
        assertEquals("Seller Nine", seller.getCorporateName());
        assertTrue(f.warehouses.findById("WH-9").isPresent());
        assertEquals("SEL-9", f.warehouses.findById("WH-9").get().getOwnerId());
    }

    @Test
    @DisplayName("A non-administrator cannot incorporate sellers and nothing is persisted")
    void nonAdminCannotIncorporate() {
        User operator = new User("OP-1", "CC-OP", "Operator", "op@nexusmarket.com", UserRole.OPERATOR_LOGISTIC, UserStatus.ACTIVE);
        assertThrows(InvalidDomainStateException.class, () -> f.incorporateSeller.incorporateSeller(newSeller(), firstWarehouse(), operator));
        assertTrue(f.users.findById("SEL-9").isEmpty());
    }

    @Test
    @DisplayName("The first warehouse must be a SELLER warehouse owned by the new seller")
    void firstWarehouseMustBelongToSeller() {
        Warehouse marketplace = new Warehouse("WH-M", "Main", new Address("Street"), WarehouseType.MARKETPLACE, null, true);
        assertThrows(InvalidDomainStateException.class, () -> f.incorporateSeller.incorporateSeller(newSeller(), marketplace, f.admin));
    }
}
