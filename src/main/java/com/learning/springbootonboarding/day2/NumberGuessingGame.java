package com.learning.springbootonboarding.day2;

import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {

    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    public static void main(String[] args) {

        System.out.println("Number Guessing Game");
        System.out.println("===================");

        boolean playAgain = true;

        while (playAgain) {
            playGame();

            System.out.println("Do You Want to play again? yes/no");

            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "yes":
                case "y":
                    System.out.println("\n Staring new game...\n");
                    break;

                case "no":
                case "n":
                    playAgain = false;
                    System.out.println("Stoping game.. Thanks for playing");
                    break;

                default:
                    System.out.println("Invalid Choice .. Exiting game");
                    playAgain = false;
                    break;
            }

        }
        scanner.close();

    }


    // Invalid Input Function
    public static int validateInput(){
        System.out.println("Enter your guess number");
        String input = scanner.nextLine();

        try {
            int guess = Integer.parseInt(input);
            if(guess<0 || guess>100){
                System.out.println("Number must be between 1 to 100 ");
                return -1;
            };
            return guess;
        } catch (Exception e) {
            System.out.println("Invalid Input, Please enter number between 1 to 100");
            return -1;
        }
    };


    // Play Game Function
    public  static void  playGame(){
        // Generate secret random number, nextInt will generate 0-99 adding one will become 1 to 100
        int secretNumber = random.nextInt(100)+1;

        // validate user input
        int guess = validateInput();

        // System.out.print(guess);

        // Stop the method if input is invalid
        if (guess == -1) {
            return;
        }

        if(guess<secretNumber){
            System.out.println("Your guess is less than secret number");
        } else if(guess>secretNumber){
            System.out.println("Your guess is higher than secret number");
        } else {
            System.out.println("Correct!, Your guess is correct");
        }

    }

}
