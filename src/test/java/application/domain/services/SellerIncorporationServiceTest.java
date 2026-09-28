package application.domain.services;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Email;
import application.domain.valueobjects.TaxIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SellerIncorporationServiceTest {

    private SellerIncorporationService incorporationService;
    private MockUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new MockUserRepository();
        incorporationService = new SellerIncorporationService(userRepository);
    }

    @Test
    @DisplayName("Should successfully incorporate seller when authorized by ADMIN")
    void shouldIncorporateSellerByAdmin() {
        Seller seller = new Seller("SEL-1", "Seller Co", new Email("seller@nexusmarket.com"), UserRole.SELLER, UserStatus.PENDING_INCORPORATION, new TaxIdentifier("TAX-999"), "Seller Corp");
        userRepository.save(seller);

        User admin = new User("ADM-1", "Admin User", new Email("admin@nexusmarket.com"), UserRole.ADMIN, UserStatus.ACTIVE);

        incorporationService.incorporateSeller("SEL-1", admin);

        assertEquals(UserStatus.ACTIVE, userRepository.findById("SEL-1").get().getStatus());
    }

    @Test
    @DisplayName("Should reject incorporation when performed by non-admin user")
    void shouldRejectNonAdminIncorporation() {
        Seller seller = new Seller("SEL-1", "Seller Co", new Email("seller@nexusmarket.com"), UserRole.SELLER, UserStatus.PENDING_INCORPORATION, new TaxIdentifier("TAX-999"), "Seller Corp");
        userRepository.save(seller);

        User buyer = new User("BUY-1", "Buyer User", new Email("buyer@nexusmarket.com"), UserRole.BUYER, UserStatus.ACTIVE);

        assertThrows(IllegalStateException.class, () ->
                incorporationService.incorporateSeller("SEL-1", buyer)
        );
    }

    private static class MockUserRepository implements UserRepositoryPort {
        private final Map<String, User> storage = new HashMap<>();

        @Override public Optional<User> findById(String identifier) { return Optional.ofNullable(storage.get(identifier)); }
        @Override public Optional<User> findByEmail(Email email) {
            return storage.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }
        @Override public User save(User user) { storage.put(user.getIdentifier(), user); return user; }
        @Override public boolean existsByEmail(Email email) { return false; }
    }
}
