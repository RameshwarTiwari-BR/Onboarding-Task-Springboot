package com.learning.springbootonboarding.day3;// Account Class

// Properties - accountHolder, AccountNumber, AccountBlance
// Methods - Deposit, Withdraw, GetBlance

// Rules -----
// 1. Can not be withdraw more than the current blance
// 2. Initial Blance Cant be in negative
// 3. Deposit Amount must be greater than 0
// 4. Blance can never be in negative

public class Account {

    private String accountHolder;  // Why private coz i dont want another class can directly change the balance or deposit/withdraw rules
    private String accountNumber;
    private double accountBalance;


    public Account(String accountHolder, String accountNumber, double initialBlance){

        if(initialBlance<0){
            throw new IllegalArgumentException("Initial balance cant be 0");
        }

        this.accountHolder = accountHolder;
        this.accountBalance = initialBlance;
        this.accountNumber = accountNumber;

    }

    // Getter for user to get their balance,
    public double getbalance(){
        return accountBalance;
    }


    // Deposit Method ---
    public void deposit(double amount){
        if(amount<0){
            throw new IllegalArgumentException("Amount cant be zero");
        }

        accountBalance+=amount;
    }

    // Withdraw Method with above rules--
    public void withdraw(double amount){
        if(amount<0){
            throw new IllegalArgumentException("amount cant be zero");
        } else if(amount>accountBalance){
            throw new IllegalArgumentException("You can not withdraw more that your account balance");
        }

        accountBalance-=amount;
    }


}




