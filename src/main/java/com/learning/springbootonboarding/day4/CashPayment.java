package com.learning.springbootonboarding.day4;

public class CashPayment implements PaymentMethod {

    @Override  // This Tells Java Means we are overiding parent interface method
    public void processPayment(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        System.out.println("Cash payment successful: " + amount);
    }
}