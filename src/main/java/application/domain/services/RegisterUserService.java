package application.domain.services;

import application.domain.exceptions.DomainException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Atomic domain service responsible exclusively for registering a new user with uniqueness validation.
 */
public class RegisterUserService {

    private final UserRepositoryPort userRepositoryPort;

    public RegisterUserService(UserRepositoryPort userRepositoryPort) {
        if (userRepositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.userRepositoryPort = userRepositoryPort;
    }

    public User registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (userRepositoryPort.findById(user.getIdentifier()).isPresent()) {
            throw new DomainException("User with identifier '" + user.getIdentifier() + "' already exists.");
        }
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new DomainException("User with email '" + user.getEmail().getValue() + "' already exists.");
        }

        return userRepositoryPort.save(user);
    }
}
