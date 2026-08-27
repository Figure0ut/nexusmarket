package com.nexusmarket.domain.model.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("Should create Money object and perform safe arithmetic")
    void shouldPerformMoneyArithmetic() {
        Money m1 = new Money(10.50);
        Money m2 = new Money(5.25);

        Money sum = m1.add(m2);
        Money diff = m1.subtract(m2);
        Money prod = m1.multiply(2);

        assertEquals(new BigDecimal("15.75"), sum.getAmount());
        assertEquals(new BigDecimal("5.25"), diff.getAmount());
        assertEquals(new BigDecimal("21.00"), prod.getAmount());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException on negative money amount")
    void shouldRejectNegativeMoney() {
        assertThrows(IllegalArgumentException.class, () -> new Money(-1.00));
    }
}
