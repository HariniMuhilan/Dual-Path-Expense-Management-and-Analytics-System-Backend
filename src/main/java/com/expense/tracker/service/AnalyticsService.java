package com.expense.tracker.service;

import com.expense.tracker.dto.*;
import com.expense.tracker.entity.CategoryType;
import com.expense.tracker.entity.Expense;
import com.expense.tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<CategoryExpenseSummaryDto> getWeeklyAnalytics(String mode) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        return getRangeAnalytics(startDate, endDate, mode);
    }

    public List<CategoryExpenseSummaryDto> getMonthlyAnalytics(int year, int month, String mode) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getRangeAnalytics(startDate, endDate, mode);
    }

    public List<CategoryExpenseSummaryDto> getYearlyAnalytics(int year, String mode) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getRangeAnalytics(startDate, endDate, mode);
    }

    public List<CategoryExpenseSummaryDto> getRangeAnalytics(LocalDate startDate, LocalDate endDate, String mode) {
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        List<Expense> expenses = fetchExpensesForRange(startDate, endDate, mode);

        Map<String, BigDecimal> categorySums = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getName(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ));

        return categorySums.entrySet().stream()
                .map(entry -> new CategoryExpenseSummaryDto(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());
    }

    public List<TimeSeriesSummaryDto> getTimeSeriesAnalytics(LocalDate startDate, LocalDate endDate, String mode) {
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        List<Expense> expenses = fetchExpensesForRange(startDate, endDate, mode);

        // Group by Year-Month (e.g. 2026-03)
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Expense>> expensesByMonth = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseDate().format(monthFormatter)));

        // Generate all consecutive months between startDate and endDate
        List<TimeSeriesSummaryDto> timeSeriesList = new ArrayList<>();
        LocalDate current = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);

        while (!current.isAfter(endMonth)) {
            String monthKey = current.format(monthFormatter);
            List<Expense> monthExpenses = expensesByMonth.getOrDefault(monthKey, Collections.emptyList());

            BigDecimal totalAmount = monthExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, BigDecimal> categoryBreakdown = monthExpenses.stream()
                    .collect(Collectors.groupingBy(
                            e -> e.getCategory().getName(),
                            Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                    ));

            timeSeriesList.add(TimeSeriesSummaryDto.builder()
                    .periodLabel(monthKey)
                    .totalAmount(totalAmount)
                    .categoryBreakdown(categoryBreakdown)
                    .build());

            current = current.plusMonths(1);
        }

        return timeSeriesList;
    }

    public ComparisonReportDto getComparisonAnalytics(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2, String mode) {
        List<Expense> expenses1 = fetchExpensesForRange(start1, end1, mode);
        List<Expense> expenses2 = fetchExpensesForRange(start2, end2, mode);

        BigDecimal total1 = expenses1.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total2 = expenses2.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal diff = total2.subtract(total1);
        Double pctChange = 0.0;
        if (total1.compareTo(BigDecimal.ZERO) > 0) {
            pctChange = diff.divide(total1, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
        } else if (total2.compareTo(BigDecimal.ZERO) > 0) {
            pctChange = 100.0;
        }

        Map<String, BigDecimal> catSums1 = expenses1.stream()
                .collect(Collectors.groupingBy(e -> e.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        Map<String, BigDecimal> catSums2 = expenses2.stream()
                .collect(Collectors.groupingBy(e -> e.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        Set<String> allCategoryNames = new TreeSet<>();
        allCategoryNames.addAll(catSums1.keySet());
        allCategoryNames.addAll(catSums2.keySet());

        List<CategoryComparisonDto> categoryComparisons = new ArrayList<>();
        for (String catName : allCategoryNames) {
            BigDecimal amt1 = catSums1.getOrDefault(catName, BigDecimal.ZERO);
            BigDecimal amt2 = catSums2.getOrDefault(catName, BigDecimal.ZERO);
            BigDecimal catDiff = amt2.subtract(amt1);

            Double catPctChange = 0.0;
            if (amt1.compareTo(BigDecimal.ZERO) > 0) {
                catPctChange = catDiff.divide(amt1, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            } else if (amt2.compareTo(BigDecimal.ZERO) > 0) {
                catPctChange = 100.0;
            }

            String trend = "EQUAL";
            if (catDiff.compareTo(BigDecimal.ZERO) > 0) {
                trend = "UP";
            } else if (catDiff.compareTo(BigDecimal.ZERO) < 0) {
                trend = "DOWN";
            }

            categoryComparisons.add(CategoryComparisonDto.builder()
                    .categoryName(catName)
                    .amountPeriod1(amt1)
                    .amountPeriod2(amt2)
                    .difference(catDiff)
                    .percentageChange(catPctChange)
                    .trend(trend)
                    .build());
        }

        categoryComparisons.sort((a, b) -> b.getAmountPeriod2().add(b.getAmountPeriod1())
                .compareTo(a.getAmountPeriod2().add(a.getAmountPeriod1())));

        List<CategoryExpenseSummaryDto> sumDto1 = catSums1.entrySet().stream()
                .map(e -> new CategoryExpenseSummaryDto(e.getKey(), e.getValue()))
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());

        List<CategoryExpenseSummaryDto> sumDto2 = catSums2.entrySet().stream()
                .map(e -> new CategoryExpenseSummaryDto(e.getKey(), e.getValue()))
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());

        PeriodSummaryDto p1 = PeriodSummaryDto.builder()
                .label(start1 + " to " + end1)
                .startDate(start1)
                .endDate(end1)
                .totalAmount(total1)
                .expenseCount(expenses1.size())
                .categorySummaries(sumDto1)
                .build();

        PeriodSummaryDto p2 = PeriodSummaryDto.builder()
                .label(start2 + " to " + end2)
                .startDate(start2)
                .endDate(end2)
                .totalAmount(total2)
                .expenseCount(expenses2.size())
                .categorySummaries(sumDto2)
                .build();

        return ComparisonReportDto.builder()
                .period1(p1)
                .period2(p2)
                .absoluteDifference(diff)
                .percentageChange(pctChange)
                .categoryComparisons(categoryComparisons)
                .build();
    }

    private List<Expense> fetchExpensesForRange(LocalDate startDate, LocalDate endDate, String mode) {
        if (mode != null && !mode.isEmpty()) {
            CategoryType categoryType = CategoryType.valueOf(mode.toUpperCase());
            return expenseRepository.findByExpenseDateBetweenAndCategory_TypeOrderByExpenseDateDesc(startDate, endDate, categoryType);
        } else {
            return expenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(startDate, endDate);
        }
    }
}
