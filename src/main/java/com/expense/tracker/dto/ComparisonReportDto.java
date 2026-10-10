package com.expense.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparisonReportDto {
    private PeriodSummaryDto period1;
    private PeriodSummaryDto period2;
    private BigDecimal absoluteDifference;
    private Double percentageChange;
    private List<CategoryComparisonDto> categoryComparisons;
}
