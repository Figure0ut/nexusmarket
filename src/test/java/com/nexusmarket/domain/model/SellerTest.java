package com.nexusmarket.domain.model.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SellerTest {

    @Test
    @DisplayName("Should create Seller in PENDING_INCORPORATION status by default")
    void shouldCreateSellerWithPendingStatus() {
        Seller seller = new Seller("SEL-101", "Sarah Seller", "sarah@nexusmarket.com");

        assertEquals("SEL-101", seller.getIdentifier());
        assertEquals(UserRole.SELLER, seller.getRole());
        assertEquals(UserStatus.PENDING_INCORPORATION, seller.getStatus());
    }

    @Test
    @DisplayName("Should allow incorporation when authorized by ADMIN user")
    void shouldIncorporateSellerWhenAuthorizedByAdmin() {
        Seller seller = new Seller("SEL-101", "Sarah Seller", "sarah@nexusmarket.com");
        User admin = new User("ADM-001", "Admin User", "admin@nexusmarket.com", UserRole.ADMIN, UserStatus.ACTIVE);

        seller.incorporate(admin);

        assertEquals(UserStatus.ACTIVE, seller.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when non-admin user attempts incorporation")
    void shouldRejectIncorporationByNonAdminUser() {
        Seller seller = new Seller("SEL-101", "Sarah Seller", "sarah@nexusmarket.com");
        User operator = new User("OP-001", "Operator User", "op@nexusmarket.com", UserRole.OPERATOR_LOGISTIC, UserStatus.ACTIVE);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                seller.incorporate(operator)
        );

        assertTrue(exception.getMessage().contains("is not authorized to incorporate sellers"));
        assertEquals(UserStatus.PENDING_INCORPORATION, seller.getStatus());
    }
}
