package com.expense.tracker.service;

import com.expense.tracker.dto.ExpenseDto;
import com.expense.tracker.entity.Category;
import com.expense.tracker.entity.Expense;
import com.expense.tracker.repository.CategoryRepository;
import com.expense.tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Expense addExpense(ExpenseDto expenseDto) {
        Category category = categoryRepository.findById(expenseDto.getCategoryId())
                .orElseThrow(() -> new com.expense.tracker.exception.CategoryNotFoundException("Category not found with id: " + expenseDto.getCategoryId()));

        Expense expense = Expense.builder()
                .amount(expenseDto.getAmount())
                .expenseDate(expenseDto.getExpenseDate())
                .description(expenseDto.getDescription())
                .category(category)
                .build();

        return expenseRepository.save(expense);
    }

    public Page<Expense> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }

    public Expense updateExpense(Long id, ExpenseDto expenseDto) {
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new com.expense.tracker.exception.ExpenseNotFoundException("Expense not found with id: " + id));

        Category category = categoryRepository.findById(expenseDto.getCategoryId())
                .orElseThrow(() -> new com.expense.tracker.exception.CategoryNotFoundException("Category not found with id: " + expenseDto.getCategoryId()));

        existingExpense.setAmount(expenseDto.getAmount());
        existingExpense.setExpenseDate(expenseDto.getExpenseDate());
        existingExpense.setDescription(expenseDto.getDescription());
        existingExpense.setCategory(category);

        return expenseRepository.save(existingExpense);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new com.expense.tracker.exception.ExpenseNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
}
