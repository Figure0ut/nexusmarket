package com.nexusmarket.domain.model.user;

import com.nexusmarket.domain.model.common.Email;

import java.util.Objects;

/**
 * Pure Java Domain Entity representing a User (Aggregate Root) in NexusMarket.
 */
public class User {

    private final String identifier;
    private String fullName;
    private Email email;
    private final UserRole role;
    private UserStatus status;

    public User(String identifier, String fullName, Email email, UserRole role, UserStatus status) {
        validateIdentifier(identifier);
        validateFullName(fullName);

        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null.");
        }
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("User status cannot be null.");
        }

        this.identifier = identifier.trim();
        this.fullName = fullName.trim();
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public User(String identifier, String fullName, String emailStr, UserRole role, UserStatus status) {
        this(identifier, fullName, new Email(emailStr), role, status);
    }

    public void updateContactInfo(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("New email cannot be null.");
        }
        this.email = newEmail;
    }

    public void changeStatus(UserStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null.");
        }
        this.status = newStatus;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    private void validateIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("User identifier cannot be null or empty.");
        }
    }

    private void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(identifier, user.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
