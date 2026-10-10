package com.expense.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodSummaryDto {
    private String label;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
    private int expenseCount;
    private List<CategoryExpenseSummaryDto> categorySummaries;
}
