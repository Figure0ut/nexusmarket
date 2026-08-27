package com.nexusmarket.domain.model.catalog;

import java.util.Objects;

/**
 * Immutable Value Object representing physical product variant options (e.g. Color: Red, Size: XL).
 */
public final class ProductVariant {

    private final String name;
    private final String value;

    public ProductVariant(String name, String value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Variant name cannot be null or empty.");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Variant value cannot be null or empty.");
        }
        this.name = name.trim();
        this.value = value.trim();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
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
