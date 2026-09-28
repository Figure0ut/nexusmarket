package application.domain.services;

import application.domain.enums.UserStatus;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidDomainStateException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.Email;

public class UserAuthenticationService {

    private final UserRepositoryPort userRepositoryPort;

    public UserAuthenticationService(UserRepositoryPort userRepositoryPort) {
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

    public void blockUser(String identifier) {
        User user = userRepositoryPort.findById(identifier)
                .orElseThrow(() -> new EntityNotFoundException("User not found with identifier: " + identifier));
        user.setStatus(UserStatus.BLOCKED);
        userRepositoryPort.save(user);
    }

    public void activateUser(String identifier) {
        User user = userRepositoryPort.findById(identifier)
                .orElseThrow(() -> new EntityNotFoundException("User not found with identifier: " + identifier));
        user.setStatus(UserStatus.ACTIVE);
        userRepositoryPort.save(user);
    }
}
