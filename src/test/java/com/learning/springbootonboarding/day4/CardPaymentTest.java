package com.learning.springbootonboarding.day4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardPaymentTest {

    @Test
    void shouldProcessValidPayment(){
        CardPayment card = new CardPayment();
        assertDoesNotThrow(()->card.processPayment(220));
    }

    @Test
    void shouldRejectZeroPayment(){
        CashPayment card = new CashPayment();
        assertThrows(IllegalArgumentException.class,
                ()->card.processPayment(-330)
        );
    }
}