package com.nexusmarket.domain.model.user;

import com.nexusmarket.domain.model.common.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should successfully create User when all invariants are satisfied")
    void shouldCreateUserWithValidData() {
        User user = new User("USR-101", "Alice Smith", "alice@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);

        assertEquals("USR-101", user.getIdentifier());
        assertEquals("Alice Smith", user.getFullName());
        assertEquals("alice@nexusmarket.com", user.getEmail().getValue());
        assertEquals(UserRole.BUYER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when full name is empty")
    void shouldFailWhenFullNameIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new User("USR-101", "   ", "alice@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE)
        );
        assertTrue(exception.getMessage().contains("Full name cannot be null or empty"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email is invalid")
    void shouldFailWhenEmailIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new User("USR-101", "Alice Smith", "invalid-email", UserRole.BUYER, UserStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("Should successfully change user status using domain business method")
    void shouldChangeUserStatus() {
        User user = new User("USR-101", "Alice Smith", "alice@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);
        user.changeStatus(UserStatus.BLOCKED);

        assertEquals(UserStatus.BLOCKED, user.getStatus());
    }

    @Test
    @DisplayName("Should successfully update contact info using domain business method")
    void shouldUpdateContactInfo() {
        User user = new User("USR-101", "Alice Smith", "alice@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);
        user.updateContactInfo(new Email("alice.new@nexusmarket.com"));

        assertEquals("alice.new@nexusmarket.com", user.getEmail().getValue());
    }
}
