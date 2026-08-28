package application.domain.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public class Money {

    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("USD");

    private BigDecimal amount;
    private Currency currency;

    public Money() {
        this.amount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.currency = DEFAULT_CURRENCY;
    }

    public Money(BigDecimal amount, Currency currency) {
        setAmount(amount);
        setCurrency(currency);
    }

    public Money(BigDecimal amount) {
        this(amount, DEFAULT_CURRENCY);
    }

    public Money(double amount) {
        this(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Multiplication factor cannot be negative.");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        ensureSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Monetary amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monetary amount cannot be negative: " + amount);
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Monetary currency cannot be null.");
        }
        this.currency = currency;
    }

    private void ensureSameCurrency(Money other) {
        if (other == null || !this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch or null operand.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount;
    }
}
