package com.learning.springbootonboarding.day4;

public class CardPayment implements PaymentMethod {

    @Override
    public void processPayment(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        System.out.println("Card payment successful: " + amount);
    }
}

