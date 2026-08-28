package application.domain.valueobjects;

import java.util.Objects;

public class Email {

    private String value;

    public Email() {
        this.value = "user@nexusmarket.com";
    }

    public Email(String value) {
        setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        String cleaned = value.trim();
        if (!cleaned.contains("@") || cleaned.startsWith("@") || cleaned.endsWith("@")) {
            throw new IllegalArgumentException("Invalid email address format: " + value);
        }
        this.value = cleaned.toLowerCase();
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
