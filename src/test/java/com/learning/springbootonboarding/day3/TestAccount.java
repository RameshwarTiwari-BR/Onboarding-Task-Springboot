package com.learning.springbootonboarding.day3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public  class TestAccount {


    @Test
    void shouldDepositAmount(){
    Account account = new Account("Rameshwar","1111",330);
        account.deposit(200);
        assertEquals(530, account.getbalance());
    }

    @Test
    void shouldNotAllowNegativeDeposit(){
    Account account = new Account("Rameshwar","1111",1000);
        assertThrows(
                IllegalArgumentException.class,
                ()->account.deposit(-100)
        );
        assertEquals(1000, account.getbalance());
    }

    @Test
    void shouldNotAllowWithdrawMoreThanAccountBalance(){
        Account account = new Account("Ram", "10101", 500);
        assertThrows(
                IllegalArgumentException.class,
                ()->account.withdraw(600)
        );
        assertEquals(500, account.getbalance());
    }
}
