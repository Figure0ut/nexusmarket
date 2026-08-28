package application.domain;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.models.User;
import application.domain.valueobjects.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Should successfully create User using getters and setters")
    void shouldCreateUserWithValidData() {
        User user = new User();
        user.setIdentifier("USR-101");
        user.setFullName("Alice Smith");
        user.setEmail(new Email("alice@nexusmarket.com"));
        user.setRole(UserRole.BUYER);
        user.setStatus(UserStatus.ACTIVE);

        assertEquals("USR-101", user.getIdentifier());
        assertEquals("Alice Smith", user.getFullName());
        assertEquals("alice@nexusmarket.com", user.getEmail().getValue());
        assertEquals(UserRole.BUYER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when full name is empty")
    void shouldFailWhenFullNameIsEmpty() {
        User user = new User();
        user.setIdentifier("USR-101");
        assertThrows(IllegalArgumentException.class, () -> user.setFullName("   "));
    }

    @Test
    @DisplayName("Should successfully change user status")
    void shouldChangeUserStatus() {
        User user = new User("USR-101", "Alice Smith", "alice@nexusmarket.com", UserRole.BUYER, UserStatus.ACTIVE);
        user.changeStatus(UserStatus.BLOCKED);

        assertEquals(UserStatus.BLOCKED, user.getStatus());
    }
}
