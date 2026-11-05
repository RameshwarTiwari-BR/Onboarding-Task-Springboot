package com.learning.springbootonboarding.day1;// Importing Scanner from util for taking input from user, its a build in Java class to read input
import java.util.Scanner;

// This is public Class
public class CelsiusConverter {

    public static void main(String[] args) {

        // Creating a obj of class Scanner
        // System.In tell scanner from where to take input like keyboard/terminal
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Celsius temperature: ");

        double celsius = scanner.nextDouble();

        // The formula of converting celsius into fahrenheit = celsius * (9/5) + 32 coz 0 celsius = 32 fah.
        double fahrenheit = (celsius * 9 / 5) + 32;

        System.out.println("Celsius: " + celsius);

        // Printing final result with formate(% means start formate) of show .2 digit, value is double/float, insert a new line
        // Its rounding number to 2 decimal place
        System.out.printf("Fahrenheit: %.2f%n", fahrenheit);

        scanner.close();
    }
}
