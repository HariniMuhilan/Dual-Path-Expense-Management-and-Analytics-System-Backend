package com.expense.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSeriesSummaryDto {
    private String periodLabel;
    private BigDecimal totalAmount;
    private Map<String, BigDecimal> categoryBreakdown;
}
