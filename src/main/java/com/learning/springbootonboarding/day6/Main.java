package com.learning.springbootonboarding.day6;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        ContactManager conacts = new ContactManager();
        Scanner scanner = new Scanner(System.in);




//        if(choice.equals("c")){
//            System.out.println("Please enter contact ID number");
//            int id = Integer.parseInt(scanner.nextLine());
//            System.out.println("Please enter your name");
//            String name = scanner.nextLine();
//            System.out.println("Enter your email");
//            String email = scanner.nextLine();
//            System.out.println("Enter your phone number");
//            String phone = scanner.nextLine();
//
//            Contact contact = new Contact(id,name,email,phone);
//            conacts.createContact(contact);
//        } else if(choice.equals("g")){
//            System.out.println("We are getting your contact details, please provide your contact ID");
//            int id = scanner.nextInt();
//            System.out.println(conacts.getContactById(id));
//        } else if(choice.equals("ga")){
//            System.out.println(conacts.getAllContacts());
//        } else {
//            System.out.println("Wrong choice, please enter c/g/ga");
//        }

        Boolean play = true;

        while (play) {
            System.out.println("Which action do u want to perform createContact C/getContactByID g/GetAllContact GA");

            String choice = scanner.nextLine().trim().toLowerCase();
            switch (choice) {
                case "c":
                    System.out.println("Please enter contact ID number");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.println("Please enter your name");
                    String name = scanner.nextLine();
                    System.out.println("Enter your email");
                    String email = scanner.nextLine();
                    System.out.println("Enter your phone number");
                    String phone = scanner.nextLine();

                    Contact contact = new Contact(id, name, email, phone);
                    conacts.createContact(contact);
                    break;
                case "g":
                    System.out.println("We are getting your contact details, please provide your contact ID");
                    int ids = scanner.nextInt();
                    System.out.println(conacts.getContactById(ids));
                    break;
                case "ga":
                    System.out.println(conacts.getAllContacts());
                    break;
                default:
                    System.out.println("Wrong choice, please enter c/g/ga");
                    break;

            }
        }

    }
}