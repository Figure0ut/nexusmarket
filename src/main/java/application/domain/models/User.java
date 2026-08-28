package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.valueobjects.Email;

import java.util.Objects;

public class User {

    private String identifier;
    private String fullName;
    private Email email;
    private UserRole role;
    private UserStatus status;

    public User() {
    }

    public User(String identifier, String fullName, Email email, UserRole role, UserStatus status) {
        setIdentifier(identifier);
        setFullName(fullName);
        setEmail(email);
        setRole(role);
        setStatus(status);
    }

    public User(String identifier, String fullName, String emailStr, UserRole role, UserStatus status) {
        this(identifier, fullName, new Email(emailStr), role, status);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("User identifier cannot be null or empty.");
        }
        this.identifier = identifier.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty.");
        }
        this.fullName = fullName.trim();
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null.");
        }
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null.");
        }
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("User status cannot be null.");
        }
        this.status = status;
    }

    public void updateContactInfo(Email newEmail) {
        setEmail(newEmail);
    }

    public void changeStatus(UserStatus newStatus) {
        setStatus(newStatus);
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
