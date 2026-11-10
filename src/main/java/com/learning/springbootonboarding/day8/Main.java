package com.learning.springbootonboarding.day8;

import com.learning.springbootonboarding.day8.model.Expense;
import com.learning.springbootonboarding.day8.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Expense> expenses = List.of(
                new Expense("Lunch",
                        new BigDecimal("250.00"),
                        LocalDate.of(2026, 11, 1),
                        ExpenseCategory.FOOD),

                new Expense("Bus",
                        new BigDecimal("50.00"),
                        LocalDate.of(2026, 11, 2),
                        ExpenseCategory.TRAVEL),

                new Expense("Groceries",
                        new BigDecimal("1200.00"),
                        LocalDate.of(2026, 11, 3),
                        ExpenseCategory.FOOD),

                new Expense("Movie",
                        new BigDecimal("350.00"),
                        LocalDate.of(2026, 11, 4),
                        ExpenseCategory.ENTERTAINMENT),

                new Expense("Electricity",
                        new BigDecimal("900.00"),
                        LocalDate.now(),
                        ExpenseCategory.BILLS)
        );
        System.out.println("Expenses:"+expenses);
    }
}