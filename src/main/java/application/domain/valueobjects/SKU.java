package application.domain.valueobjects;

import java.util.Objects;

public class SKU {

    private String code;

    public SKU() {
        this.code = "SKU-DEFAULT";
    }

    public SKU(String code) {
        setCode(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU code cannot be null or empty.");
        }
        this.code = code.trim().toUpperCase();
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
