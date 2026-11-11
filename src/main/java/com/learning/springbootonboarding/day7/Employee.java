package com.learning.springbootonboarding.day7;

public class Employee {
    private final String name;
    private final long salary;
    private final String department;

//    Constructor for employee
    public Employee(String name, long salary, String department) {
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

//    Encapsulation getter+setter
    public String getName(){
        return name;
    }

    public long getSalary(){
        return salary;
    }

    public  String getDepartment(){
        return department;
    }
}
