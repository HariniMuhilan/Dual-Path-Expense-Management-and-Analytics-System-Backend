package com.expense.tracker.service;

import com.expense.tracker.dto.CategoryExpenseSummaryDto;
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

    public List<CategoryExpenseSummaryDto> getWeeklyAnalytics() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        return getAnalytics(startDate, endDate);
    }

    public List<CategoryExpenseSummaryDto> getMonthlyAnalytics(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getAnalytics(startDate, endDate);
    }

    private List<CategoryExpenseSummaryDto> getAnalytics(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);

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
