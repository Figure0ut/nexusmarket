package application.domain.valueobjects;

import java.util.Objects;

public class StockQuantity {

    private int value;

    public StockQuantity() {
        this.value = 0;
    }

    public StockQuantity(int value) {
        setValue(value);
    }

    public static StockQuantity zero() {
        return new StockQuantity(0);
    }

    public StockQuantity add(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Addition delta cannot be negative: " + delta);
        }
        return new StockQuantity(this.value + delta);
    }

    public StockQuantity subtract(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Subtraction delta cannot be negative: " + delta);
        }
        if (this.value < delta) {
            throw new IllegalArgumentException("Insufficient stock quantity. Available: " + this.value + ", requested: " + delta);
        }
        return new StockQuantity(this.value - delta);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative: " + value);
        }
        this.value = value;
    }

    public boolean isZero() {
        return value == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockQuantity that = (StockQuantity) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
