package com.expense.tracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseDto {
    private Long id;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private Long categoryId;
}
