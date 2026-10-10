package com.expense.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryComparisonDto {
    private String categoryName;
    private BigDecimal amountPeriod1;
    private BigDecimal amountPeriod2;
    private BigDecimal difference;
    private Double percentageChange;
    private String trend; // "UP", "DOWN", "EQUAL"
}
