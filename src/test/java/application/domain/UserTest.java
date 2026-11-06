package application.domain;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("A user requires identifier, identity document, non-empty name, email, role and status")
    void createValidUser() {
        User user = new User("USR-101", "CC-101", "Alice Smith", "Alice@NexusMarket.com", UserRole.SUPERVISOR, UserStatus.ACTIVE);
        assertEquals("USR-101", user.getIdentifier());
        assertEquals("CC-101", user.getIdentityDocument());
        assertEquals("alice@nexusmarket.com", user.getEmail().getValue());
        assertEquals(UserRole.SUPERVISOR, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Full name cannot be empty")
    void rejectEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("USR-1", "CC-1", "  ", "a@b.com", UserRole.ADMIN, UserStatus.ACTIVE));
    }

    @Test
    @DisplayName("Identity document is mandatory")
    void rejectMissingIdentityDocument() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("USR-1", null, "Name", "a@b.com", UserRole.ADMIN, UserStatus.ACTIVE));
    }

    @Test
    @DisplayName("Role and status are mandatory")
    void rejectMissingRoleOrStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("USR-1", "CC-1", "Name", "a@b.com", null, UserStatus.ACTIVE));
        assertThrows(IllegalArgumentException.class,
                () -> new User("USR-1", "CC-1", "Name", "a@b.com", UserRole.ADMIN, null));
    }

    @Test
    @DisplayName("Invalid email format is rejected")
    void rejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("USR-1", "CC-1", "Name", "not-an-email", UserRole.ADMIN, UserStatus.ACTIVE));
    }
}
