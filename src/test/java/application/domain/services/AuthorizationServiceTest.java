package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.AuthorizationException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.services.ValidateAuthorizationService.BusinessProcess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTest {

    private final ValidateAuthorizationService service = new ValidateAuthorizationService();

    private User user(String id, UserRole role) {
        return new User(id, "DOC-" + id, "Name " + id, id.toLowerCase() + "@nexusmarket.com", role, UserStatus.ACTIVE);
    }

    private final User buyer = user("BUY-1", UserRole.BUYER);
    private final User seller = user("SEL-1", UserRole.SELLER);
    private final User operator = user("OP-1", UserRole.OPERATOR_LOGISTIC);
    private final User admin = user("ADM-1", UserRole.ADMIN);
    private final User supervisor = user("SUP-1", UserRole.SUPERVISOR);

    @Test
    @DisplayName("Responsibility matrix (section 12) is applied exactly")
    void responsibilityMatrix() {
        // Seller registration: Admin
        assertTrue(service.isAuthorized(admin, BusinessProcess.SELLER_REGISTRATION));
        assertFalse(service.isAuthorized(supervisor, BusinessProcess.SELLER_REGISTRATION));
        assertFalse(service.isAuthorized(seller, BusinessProcess.SELLER_REGISTRATION));
        // Product registration: Seller
        assertTrue(service.isAuthorized(seller, BusinessProcess.PRODUCT_REGISTRATION));
        assertFalse(service.isAuthorized(admin, BusinessProcess.PRODUCT_REGISTRATION));
        // Inventory administration: Seller, Logistics Operator
        assertTrue(service.isAuthorized(seller, BusinessProcess.INVENTORY_MANAGEMENT));
        assertTrue(service.isAuthorized(operator, BusinessProcess.INVENTORY_MANAGEMENT));
        assertFalse(service.isAuthorized(buyer, BusinessProcess.INVENTORY_MANAGEMENT));
        assertFalse(service.isAuthorized(admin, BusinessProcess.INVENTORY_MANAGEMENT));
        // Order management: Buyer, Seller, Logistics Operator
        assertTrue(service.isAuthorized(buyer, BusinessProcess.ORDER_MANAGEMENT));
        assertTrue(service.isAuthorized(seller, BusinessProcess.ORDER_MANAGEMENT));
        assertTrue(service.isAuthorized(operator, BusinessProcess.ORDER_MANAGEMENT));
        assertFalse(service.isAuthorized(admin, BusinessProcess.ORDER_MANAGEMENT));
        assertFalse(service.isAuthorized(supervisor, BusinessProcess.ORDER_MANAGEMENT));
        // Refund management: Buyer, Admin
        assertTrue(service.isAuthorized(buyer, BusinessProcess.REFUND_MANAGEMENT));
        assertTrue(service.isAuthorized(admin, BusinessProcess.REFUND_MANAGEMENT));
        assertFalse(service.isAuthorized(supervisor, BusinessProcess.REFUND_MANAGEMENT));
    }

    @Test
    @DisplayName("The supervisor can only consult (never execute) operations")
    void supervisorOnlyConsults() {
        assertDoesNotThrow(() -> service.validateQueryAuthorization(supervisor, BusinessProcess.ORDER_MANAGEMENT));
        assertThrows(AuthorizationException.class, () -> service.validateProcessAuthorization(supervisor, BusinessProcess.ORDER_MANAGEMENT));
        assertDoesNotThrow(() -> service.validateProcessAuthorization(supervisor, BusinessProcess.ADMINISTRATIVE_REPORTS));
    }

    @Test
    @DisplayName("RG-03: buyers and sellers only manage their own information")
    void ownership() {
        assertDoesNotThrow(() -> service.validateOwnership(buyer, "BUY-1"));
        assertThrows(AuthorizationException.class, () -> service.validateOwnership(buyer, "BUY-2"));
        assertThrows(AuthorizationException.class, () -> service.validateOwnership(seller, "SEL-2"));
        assertDoesNotThrow(() -> service.validateOwnership(operator, "SEL-2"));
    }

    @Test
    @DisplayName("RG-01: an unauthenticated (null) or non-active actor is rejected")
    void actorMustBeAuthenticatedAndActive() {
        assertThrows(IllegalArgumentException.class, () -> service.validateProcessAuthorization(null, BusinessProcess.ORDER_MANAGEMENT));
        User blocked = new User("ADM-2", "DOC-2", "Blocked", "b@nexusmarket.com", UserRole.ADMIN, UserStatus.BLOCKED);
        assertThrows(InvalidDomainStateException.class, () -> service.validateProcessAuthorization(blocked, BusinessProcess.SELLER_REGISTRATION));
    }
}
