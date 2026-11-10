package com.learning.springbootonboarding.day4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CashPaymentTest {

    @Test
    void shouldProcessValidPayment() {
        CashPayment cash = new CashPayment();
        assertDoesNotThrow(() -> cash.processPayment(2000));
    }

    @Test
    void shouldRejectNegativePayment() {
        CashPayment cash = new CashPayment();
        assertThrows(
                IllegalArgumentException.class,
                () -> cash.processPayment(-200)
        );
    }

    @Test
    void shouldRejectZeroPayment() {
        CashPayment cash = new CashPayment();
        assertThrows(
                IllegalArgumentException.class,
                () -> cash.processPayment(0)
        );
    }
}