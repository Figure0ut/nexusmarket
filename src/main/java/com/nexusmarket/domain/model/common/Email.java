package com.nexusmarket.domain.model.common;

import java.util.Objects;

/**
 * Immutable Value Object encapsulating email validation in NexusMarket.
 */
public final class Email {

    private final String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        String cleaned = value.trim();
        if (!cleaned.contains("@") || cleaned.startsWith("@") || cleaned.endsWith("@")) {
            throw new IllegalArgumentException("Invalid email address format: " + value);
        }
        this.value = cleaned.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
