package com.expense.tracker.repository;

import com.expense.tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    java.util.List<Expense> findByExpenseDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
    java.util.List<Expense> findByExpenseDateBetweenAndCategory_Type(java.time.LocalDate startDate, java.time.LocalDate endDate, com.expense.tracker.entity.CategoryType type);
}
