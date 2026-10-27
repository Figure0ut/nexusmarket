package application.domain.services;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Atomic domain service responsible exclusively for querying a user by identifier.
 */
public class GetUserService {

    private final UserRepositoryPort userRepositoryPort;

    public GetUserService(UserRepositoryPort userRepositoryPort) {
        if (userRepositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.userRepositoryPort = userRepositoryPort;
    }

    public User getUser(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("User identifier cannot be null or empty.");
        }
        return userRepositoryPort.findById(identifier.trim())
                .orElseThrow(() -> new EntityNotFoundException("User not found with identifier: " + identifier));
    }
}
