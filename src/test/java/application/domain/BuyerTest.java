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

    @Test
    @DisplayName("Should successfully create Buyer entity with main address and commercial status")
    void shouldCreateBuyerWithValidData() {
        Buyer buyer = new Buyer("BUY-101", "Bob Buyer", "bob@nexusmarket.com", "123 Main St", "STANDARD");

        assertEquals("BUY-101", buyer.getIdentifier());
        assertEquals("Bob Buyer", buyer.getFullName());
        assertEquals("bob@nexusmarket.com", buyer.getEmail().getValue());
        assertEquals(UserRole.BUYER, buyer.getRole());
        assertEquals(UserStatus.ACTIVE, buyer.getStatus());
        assertEquals("123 Main St", buyer.getMainAddress().getStreet());
        assertEquals("STANDARD", buyer.getCommercialStatus());
        assertTrue(buyer.getAdditionalAddresses().isEmpty());
    }

    @Test
    @DisplayName("Should update main address and add secondary addresses")
    void shouldManageBuyerAddresses() {
        Buyer buyer = new Buyer("BUY-101", "Bob Buyer", "bob@nexusmarket.com", "123 Main St", "STANDARD");
        buyer.updateMainAddress(new Address("456 Broadway Ave"));
        buyer.addSecondaryAddress(new Address("789 Secondary St"));

        assertEquals("456 Broadway Ave", buyer.getMainAddress().getStreet());
        assertEquals(1, buyer.getAdditionalAddresses().size());
        assertEquals("789 Secondary St", buyer.getAdditionalAddresses().get(0).getStreet());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when Buyer attempts to administer another Buyer or User")
    void shouldRejectBuyerAdminOperationOnTarget() {
        Buyer buyer = new Buyer("BUY-101", "Bob Buyer", "bob@nexusmarket.com", "123 Main St", "STANDARD");
        User targetBuyer = new User("BUY-102", "Charlie Buyer", "charlie@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                buyer.validateOperation(targetBuyer)
        );
        assertTrue(exception.getMessage().contains("is not authorized to perform administrative operations"));
    }
}
