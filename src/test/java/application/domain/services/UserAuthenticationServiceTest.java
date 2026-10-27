package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationServiceTest {

    private AuthenticateUserService authenticateUserService;
    private RegisterUserService registerUserService;
    private BlockUserService blockUserService;
    private ActivateUserService activateUserService;

    private MockUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new MockUserRepository();
        authenticateUserService = new AuthenticateUserService(userRepository);
        registerUserService = new RegisterUserService(userRepository);
        blockUserService = new BlockUserService(userRepository);
        activateUserService = new ActivateUserService(userRepository);
    }

    @Test
    @DisplayName("Should authenticate active user and reject blocked user via atomic services")
    void shouldAuthenticateAndEnforceStatus() {
        User activeUser = new User("U-1", "John Doe", new Email("john@nexusmarket.com"), UserRole.BUYER, UserStatus.ACTIVE);
        userRepository.save(activeUser);

        User authenticated = authenticateUserService.authenticate(new Email("john@nexusmarket.com"));
        assertNotNull(authenticated);
        assertEquals("U-1", authenticated.getIdentifier());

        blockUserService.blockUser("U-1");
        assertThrows(InvalidDomainStateException.class, () ->
                authenticateUserService.authenticate(new Email("john@nexusmarket.com"))
        );
    }

    @Test
    @DisplayName("Should register new user and reject duplicates via atomic services")
    void shouldRegisterAndRejectDuplicates() {
        User user = new User("U-1", "John Doe", new Email("john@nexusmarket.com"), UserRole.BUYER, UserStatus.ACTIVE);
        registerUserService.registerUser(user);

        User dupId = new User("U-1", "Other", new Email("other@nexusmarket.com"), UserRole.BUYER, UserStatus.ACTIVE);
        assertThrows(DomainException.class, () -> registerUserService.registerUser(dupId));

        User dupEmail = new User("U-2", "Other", new Email("john@nexusmarket.com"), UserRole.BUYER, UserStatus.ACTIVE);
        assertThrows(DomainException.class, () -> registerUserService.registerUser(dupEmail));
    }

    private static class MockUserRepository implements UserRepositoryPort {
        private final Map<String, User> storage = new HashMap<>();

        @Override public Optional<User> findById(String identifier) { return Optional.ofNullable(storage.get(identifier)); }
        @Override public Optional<User> findByEmail(Email email) {
            return storage.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }
        @Override public User save(User user) { storage.put(user.getIdentifier(), user); return user; }
        @Override public boolean existsByEmail(Email email) {
            return storage.values().stream().anyMatch(u -> u.getEmail().equals(email));
        }
    }
}
