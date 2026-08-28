package application.domain.valueobjects;

import java.util.Objects;

public class TaxIdentifier {

    private String taxId;

    public TaxIdentifier() {
        this.taxId = "TAX-DEFAULT";
    }

    public TaxIdentifier(String taxId) {
        setTaxId(taxId);
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        if (taxId == null || taxId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tax Identifier cannot be null or empty.");
        }
        this.taxId = taxId.trim().toUpperCase();
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
