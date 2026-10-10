package com.expense.tracker.repository;

import com.expense.tracker.entity.CategoryType;
import com.expense.tracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);
    List<Expense> findByExpenseDateBetweenAndCategory_Type(LocalDate startDate, LocalDate endDate, CategoryType type);
    List<Expense> findByExpenseDateBetweenAndCategory_TypeOrderByExpenseDateDesc(LocalDate startDate, LocalDate endDate, CategoryType type);
    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(LocalDate startDate, LocalDate endDate);
    List<Expense> findByCategory_TypeOrderByExpenseDateDesc(CategoryType type);
    Page<Expense> findByCategory_Type(CategoryType type, Pageable pageable);
}
