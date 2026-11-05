package com.learning.springbootonboarding.day3;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Account account = new Account("Ram", "12345", 0);

        System.out.println("Which action do u want to perform Balance b/Withdraw w/Deposit d");

        String cases = scanner.nextLine().trim().toLowerCase();

        switch (cases) {
            case "balance":
            case "b":
                System.out.println("Your Balance"+ account.getbalance());
                break;

            case "withdraw":
            case "w":
                System.out.println("Please Enter amount to withdraw");
                String withdrawAmount = scanner.nextLine().trim();
                int withdrawAmounts = Integer.parseInt(withdrawAmount);
                account.withdraw(withdrawAmounts);
                System.out.println("After withdraw"+ account.getbalance());


            case "deposit":
            case "d":
                System.out.println("Please Enter amount to deposit");
                String depositAmount = scanner.nextLine().trim();
                int depositAmounts = Integer.parseInt(depositAmount);
                account.withdraw(depositAmounts);
                System.out.println("After Deposit"+ account.getbalance());


            default:
                System.out.println("Invalid Choice, please Type balance/b or withdraw/w or deposit/d");
                break;
        }

        // System.out.println("InitialBalance"+ account.getbalance());
        // account.deposit(2000);
        // System.out.println("After Deposit Blance"+ account.getbalance());
        // account.withdraw(1000);
        // System.out.println("After withdraw"+ account.getbalance());

    }
}
