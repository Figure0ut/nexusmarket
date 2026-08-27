package com.nexusmarket.domain.model.common;

import java.util.Objects;

/**
 * Immutable Value Object representing corporate tax numbers for Sellers and Invoicing.
 */
public final class TaxIdentifier {

    private final String taxId;

    public TaxIdentifier(String taxId) {
        if (taxId == null || taxId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tax Identifier cannot be null or empty.");
        }
        this.taxId = taxId.trim().toUpperCase();
    }

    public String getTaxId() {
        return taxId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxIdentifier that = (TaxIdentifier) o;
        return Objects.equals(taxId, that.taxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxId);
    }

    @Override
    public String toString() {
        return taxId;
    }
}
