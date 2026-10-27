package application.domain.services;

import application.domain.enums.UserStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;

/**
 * Atomic domain service responsible exclusively for blocking a user account.
 */
public class BlockUserService {

    private final UserRepositoryPort userRepositoryPort;

    public BlockUserService(UserRepositoryPort userRepositoryPort) {
        if (userRepositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.userRepositoryPort = userRepositoryPort;
    }

    public void blockUser(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("User identifier cannot be null or empty.");
        }
        User user = userRepositoryPort.findById(identifier.trim())
                .orElseThrow(() -> new EntityNotFoundException("User not found with identifier: " + identifier));
        user.setStatus(UserStatus.BLOCKED);
        userRepositoryPort.save(user);
    }
}
