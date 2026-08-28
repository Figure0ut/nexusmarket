package application.domain.valueobjects;

import java.util.Objects;

public class ProductVariant {

    private String name;
    private String value;

    public ProductVariant() {
        this.name = "Default";
        this.value = "Default";
    }

    public ProductVariant(String name, String value) {
        setName(name);
        setValue(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Variant name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Variant value cannot be null or empty.");
        }
        this.value = value.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariant that = (ProductVariant) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
