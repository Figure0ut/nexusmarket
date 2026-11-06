package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.valueobjects.Email;
import application.infrastructure.adapters.out.memory.InMemoryUserRepositoryAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationServiceTest {

    private final InMemoryUserRepositoryAdapter repository = new InMemoryUserRepositoryAdapter();
    private final RegisterUserService registerUserService = new RegisterUserService(repository);
    private final AuthenticateUserService authenticateUserService = new AuthenticateUserService(repository);
    private final BlockUserService blockUserService = new BlockUserService(repository);

    private User user(String id, String doc, String email) {
        return new User(id, doc, "Name", new Email(email), UserRole.SUPERVISOR, UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Identifier, identity document and email must be unique (section 11)")
    void uniqueness() {
        registerUserService.registerUser(user("U-1", "CC-1", "john@nexusmarket.com"));
        assertThrows(DomainException.class, () -> registerUserService.registerUser(user("U-1", "CC-2", "other@nexusmarket.com")));
        assertThrows(DomainException.class, () -> registerUserService.registerUser(user("U-2", "CC-1", "other@nexusmarket.com")));
        assertThrows(DomainException.class, () -> registerUserService.registerUser(user("U-2", "CC-2", "JOHN@nexusmarket.com")));
    }

    @Test
    @DisplayName("Only ACTIVE users can authenticate")
    void blockedUserCannotAuthenticate() {
        registerUserService.registerUser(user("U-1", "CC-1", "john@nexusmarket.com"));
        assertEquals("U-1", authenticateUserService.authenticate(new Email("john@nexusmarket.com")).getIdentifier());
        blockUserService.blockUser("U-1");
        assertThrows(InvalidDomainStateException.class, () -> authenticateUserService.authenticate(new Email("john@nexusmarket.com")));
    }
}
