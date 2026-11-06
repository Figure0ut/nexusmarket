package application.domain;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuyerTest {

    private Buyer buyer() {
        return new Buyer("BUY-101", "CC-101", "Bob Buyer", "bob@nexusmarket.com", "123 Main St", "ENABLED");
    }

    @Test
    @DisplayName("A buyer always has the BUYER role and a mandatory main address")
    void buyerHasBuyerRole() {
        Buyer buyer = buyer();
        assertEquals(UserRole.BUYER, buyer.getRole());
        assertEquals("123 Main St", buyer.getMainAddress().getStreet());
        assertThrows(IllegalArgumentException.class, () -> buyer.setRole(UserRole.ADMIN));
    }

    @Test
    @DisplayName("Additional addresses are optional and cannot duplicate the main address")
    void additionalAddresses() {
        Buyer buyer = buyer();
        assertTrue(buyer.getAdditionalAddresses().isEmpty());
        buyer.addSecondaryAddress(new Address("456 Office Ave"));
        assertEquals(1, buyer.getAdditionalAddresses().size());
        assertThrows(IllegalArgumentException.class, () -> buyer.addSecondaryAddress(new Address("123 Main St")));
    }

    @Test
    @DisplayName("Commercial status is mandatory")
    void commercialStatusMandatory() {
        assertThrows(IllegalArgumentException.class,
                () -> new Buyer("BUY-1", "CC-1", "Name", "a@b.com", "Street 1", " "));
    }

    @Test
    @DisplayName("A buyer cannot operate on another buyer's account")
    void buyerCannotOperateOnOthers() {
        Buyer buyer = buyer();
        User other = new User("BUY-102", "CC-102", "Charlie", "charlie@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);
        assertThrows(IllegalStateException.class, () -> buyer.validateOperation(other));
        assertDoesNotThrow(() -> buyer.validateOperation(buyer));
    }
}
