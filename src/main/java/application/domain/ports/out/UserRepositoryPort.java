package application.domain.ports.out;

import application.domain.models.User;
import application.domain.valueobjects.Email;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(String identifier);
    Optional<User> findByEmail(Email email);
    User save(User user);
    boolean existsByEmail(Email email);
}
