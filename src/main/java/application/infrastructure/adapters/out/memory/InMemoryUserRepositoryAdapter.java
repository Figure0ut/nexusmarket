package application.infrastructure.adapters.out.memory;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Email;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepositoryAdapter implements UserRepositoryPort {

    private final Map<String, User> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findById(String identifier) {
        return Optional.ofNullable(storage.get(identifier));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return storage.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User save(User user) {
        storage.put(user.getIdentifier(), user);
        return user;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return storage.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }
}
