package com.learning.springbootonboarding.day7;

import com.learning.springbootonboarding.day6.Contact;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//StreamAPi is used to perform action on data, just like tradition loop with filter/groupby/etc operations

public class StreamApi {
//    Making this logger private so other class file cant write log into same files..
    private static final Logger logger = LoggerFactory.getLogger(StreamApi.class);

    public static void main(String[] args) {

        List<Contact> contacts = List.of(
                new Contact(1, "Ram", "ram@gmail.com", "123456"),
                new Contact(2, "Rahul", "rahul@gmail.com", "234567"),
                new Contact(3, "Ram", "ram2@gmail.com", "345678"),
                new Contact(4, "Amit", "amit@gmail.com", "456789"),
                new Contact(5, "Priya", "priya@gmail.com", "567890")
        );

        List<Employee> employees = List.of(
                new Employee("Ram", 200, "IT"),
                new Employee("Rahu", 300, "HR"),
                new Employee("Vikash", 400, "IT"),
                new Employee("Test", 100, "FN"),
                new Employee("Shubham", 500, "IT")
        );


//        Here  all contacts whose names are Ram
        List<Contact> ramContacts = contacts.stream()
                .filter(contact -> contact.getName().equals("Ram"))
                .toList();

//        Count total contact
        long totalContact = contacts.stream().count();

//        Total Salary by department
        Map<String, Long> totalSalaryByDepartment = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.summingLong(Employee::getSalary)
                ));

//        Replaced system.out.println with logger day 9 task
        logger.info("Ram Contact{}",ramContacts);
        logger.info("totalContact:{}", totalContact);
        logger.info("TotalSalaryByDepartment:{}", totalSalaryByDepartment);
    }
}
