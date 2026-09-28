package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTest {

    private AuthorizationService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthorizationService();
    }

    @Test
    @DisplayName("Should validate authorization according to Responsibility Matrix")
    void shouldAuthorizeAllowedRoles() {
        User admin = new User("ADM-1", "Admin", "admin@nexusmarket.com", UserRole.ADMIN, UserStatus.ACTIVE);
        User seller = new User("SEL-1", "Seller", "seller@nexusmarket.com", UserRole.SELLER, UserStatus.ACTIVE);
        User buyer = new User("BUY-1", "Buyer", "buyer@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);
        User operator = new User("OP-1", "Operator", "op@nexusmarket.com", UserRole.OPERATOR_LOGISTIC, UserStatus.ACTIVE);

        // Admin can register sellers
        assertDoesNotThrow(() -> authService.validateProcessAuthorization(admin, AuthorizationService.BusinessProcess.SELLER_REGISTRATION));

        // Seller can register products
        assertDoesNotThrow(() -> authService.validateProcessAuthorization(seller, AuthorizationService.BusinessProcess.PRODUCT_REGISTRATION));

        // Operator can manage inventory
        assertDoesNotThrow(() -> authService.validateProcessAuthorization(operator, AuthorizationService.BusinessProcess.INVENTORY_MANAGEMENT));

        // Buyer can manage orders
        assertDoesNotThrow(() -> authService.validateProcessAuthorization(buyer, AuthorizationService.BusinessProcess.ORDER_MANAGEMENT));
    }

    @Test
    @DisplayName("Should reject unauthorized role for business process (RG-03)")
    void shouldRejectUnauthorizedRole() {
        User buyer = new User("BUY-1", "Buyer", "buyer@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);

        // Buyer cannot register products (Seller only)
        assertThrows(InvalidDomainStateException.class, () ->
                authService.validateProcessAuthorization(buyer, AuthorizationService.BusinessProcess.PRODUCT_REGISTRATION)
        );
    }

    @Test
    @DisplayName("Should reject inactive or blocked user (RG-01)")
    void shouldRejectBlockedUser() {
        User blockedAdmin = new User("ADM-1", "Admin", "admin@nexusmarket.com", UserRole.ADMIN, UserStatus.BLOCKED);

        assertThrows(InvalidDomainStateException.class, () ->
                authService.validateProcessAuthorization(blockedAdmin, AuthorizationService.BusinessProcess.SELLER_REGISTRATION)
        );
    }
}
