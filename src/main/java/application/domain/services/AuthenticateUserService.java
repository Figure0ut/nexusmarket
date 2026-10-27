package application.domain.services;

import application.domain.enums.UserStatus;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Email;

/**
 * Atomic domain service responsible exclusively for authenticating active users.
 */
public class AuthenticateUserService {

    private final UserRepositoryPort userRepositoryPort;

    public AuthenticateUserService(UserRepositoryPort userRepositoryPort) {
        if (userRepositoryPort == null) {
            throw new IllegalArgumentException("UserRepositoryPort cannot be null.");
        }
        this.userRepositoryPort = userRepositoryPort;
    }

    public User authenticate(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null.");
        }
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email.getValue()));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new InvalidDomainStateException("Authentication failure: User account '" + user.getIdentifier() + "' is BLOCKED.");
        }
        if (user.getStatus() == UserStatus.PENDING_INCORPORATION) {
            throw new InvalidDomainStateException("Authentication failure: User account '" + user.getIdentifier() + "' is PENDING_INCORPORATION.");
        }

        return user;
    }
}
