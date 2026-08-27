package com.nexusmarket.domain.model.common;

import java.util.Objects;

/**
 * Immutable Value Object representing a Stock Keeping Unit (SKU) in NexusMarket catalog.
 */
public final class SKU {

    private final String code;

    public SKU(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU code cannot be null or empty.");
        }
        this.code = code.trim().toUpperCase();
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SKU sku = (SKU) o;
        return Objects.equals(code, sku.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
