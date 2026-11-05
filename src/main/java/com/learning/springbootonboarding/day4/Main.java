package com.learning.springbootonboarding.day4;

public class Main {

    // Create a cash payement object
    // Create a card payment object

    public static void main(String[] args) {

        CashPayment cash = new CashPayment();

        cash.processPayment(500);

        CardPayment card = new CardPayment();

        card.processPayment(300);
    }
}
