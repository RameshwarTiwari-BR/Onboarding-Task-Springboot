package com.learning.springbootonboarding.day8.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Expense(
        String title,
        BigDecimal amount,
        LocalDate date,
        ExpenseCategory category
) {}

