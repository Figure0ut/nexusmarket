package application.domain;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.models.Seller;
import application.domain.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SellerTest {

    private Seller seller() {
        return new Seller("SEL-101", "NIT-101", "Sarah Seller", "sarah@nexusmarket.com", "TAX-101", "Sarah Corp");
    }

    @Test
    @DisplayName("A new seller is pending incorporation")
    void newSellerIsPending() {
        Seller seller = seller();
        assertEquals(UserRole.SELLER, seller.getRole());
        assertEquals(UserStatus.PENDING_INCORPORATION, seller.getStatus());
    }

    @Test
    @DisplayName("An ACTIVE administrator incorporates the seller")
    void adminIncorporatesSeller() {
        Seller seller = seller();
        User admin = new User("ADM-001", "CC-1", "Admin", "admin@nexusmarket.com", UserRole.ADMIN, UserStatus.ACTIVE);
        seller.incorporate(admin);
        assertEquals(UserStatus.ACTIVE, seller.getStatus());
        assertThrows(IllegalStateException.class, () -> seller.incorporate(admin));
    }

    @Test
    @DisplayName("Only an administrator can incorporate sellers (sellers cannot self-register)")
    void nonAdminCannotIncorporate() {
        Seller seller = seller();
        User operator = new User("OP-001", "CC-2", "Operator", "op@nexusmarket.com", UserRole.OPERATOR_LOGISTIC, UserStatus.ACTIVE);
        assertThrows(IllegalStateException.class, () -> seller.incorporate(operator));
        assertThrows(IllegalStateException.class, () -> seller.incorporate(seller));
    }
}
