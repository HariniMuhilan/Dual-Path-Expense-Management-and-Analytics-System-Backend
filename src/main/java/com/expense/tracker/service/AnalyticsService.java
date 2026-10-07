package com.expense.tracker.service;

import com.expense.tracker.dto.CategoryExpenseSummaryDto;
import com.expense.tracker.entity.CategoryType;
import com.expense.tracker.entity.Expense;
import com.expense.tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<CategoryExpenseSummaryDto> getWeeklyAnalytics(String mode) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        return getAnalytics(startDate, endDate, mode);
    }

    public List<CategoryExpenseSummaryDto> getMonthlyAnalytics(int year, int month, String mode) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getAnalytics(startDate, endDate, mode);
    }

    public List<CategoryExpenseSummaryDto> getYearlyAnalytics(int year, String mode) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getAnalytics(startDate, endDate, mode);
    }

    private List<CategoryExpenseSummaryDto> getAnalytics(LocalDate startDate, LocalDate endDate, String mode) {
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
        if (startDate.isBefore(twoYearsAgo)) {
            throw new RuntimeException("Cannot fetch data older than 2 years");
        }

        List<Expense> expenses;
        if (mode != null && !mode.isEmpty()) {
            CategoryType categoryType = CategoryType.valueOf(mode.toUpperCase());
            expenses = expenseRepository.findByExpenseDateBetweenAndCategory_Type(startDate, endDate, categoryType);
        } else {
            expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);
        }

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
                .collect(Collectors.toList());
    }
}
