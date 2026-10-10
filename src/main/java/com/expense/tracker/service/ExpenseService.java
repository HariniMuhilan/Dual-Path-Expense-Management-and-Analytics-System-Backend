package com.expense.tracker.service;

import com.expense.tracker.dto.ExpenseDto;
import com.expense.tracker.entity.Category;
import com.expense.tracker.entity.CategoryType;
import com.expense.tracker.entity.Expense;
import com.expense.tracker.repository.CategoryRepository;
import com.expense.tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public List<Expense> getExpensesByFilter(LocalDate startDate, LocalDate endDate, String mode) {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                LocalDate temp = startDate;
                startDate = endDate;
                endDate = temp;
            }
            if (mode != null && !mode.isEmpty()) {
                CategoryType categoryType = CategoryType.valueOf(mode.toUpperCase());
                return expenseRepository.findByExpenseDateBetweenAndCategory_TypeOrderByExpenseDateDesc(startDate, endDate, categoryType);
            } else {
                return expenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(startDate, endDate);
            }
        }

        if (mode != null && !mode.isEmpty()) {
            CategoryType categoryType = CategoryType.valueOf(mode.toUpperCase());
            return expenseRepository.findByCategory_TypeOrderByExpenseDateDesc(categoryType);
        }

        return expenseRepository.findAll();
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

    public int seedDemoExpenses() {
        List<Category> householdCats = categoryRepository.findByType(CategoryType.HOUSEHOLD);
        List<Category> businessCats = categoryRepository.findByType(CategoryType.BUSINESS);

        if (householdCats.isEmpty() || businessCats.isEmpty()) {
            return 0;
        }

        List<Expense> demoList = new ArrayList<>();
        Random random = new Random(42);

        LocalDate today = LocalDate.now();
        // Generate expenses over the past 36 months (3 years)
        for (int m = 0; m <= 36; m++) {
            LocalDate monthDate = today.minusMonths(m);
            int year = monthDate.getYear();
            int month = monthDate.getMonthValue();

            // Seed Household expenses
            for (Category cat : householdCats) {
                BigDecimal baseAmount;
                switch (cat.getName()) {
                    case "Rent":
                        baseAmount = BigDecimal.valueOf(25000);
                        break;
                    case "EMI":
                        baseAmount = BigDecimal.valueOf(15000);
                        break;
                    case "Investment":
                        baseAmount = BigDecimal.valueOf(10000 + random.nextInt(5000));
                        break;
                    case "Groceries":
                        baseAmount = BigDecimal.valueOf(7000 + random.nextInt(5000));
                        break;
                    case "Bills":
                        baseAmount = BigDecimal.valueOf(3000 + random.nextInt(2500));
                        break;
                    case "Food":
                        baseAmount = BigDecimal.valueOf(4000 + random.nextInt(4000));
                        break;
                    case "Travel":
                        baseAmount = BigDecimal.valueOf(2000 + random.nextInt(3500));
                        break;
                    case "Salary for workers":
                        baseAmount = BigDecimal.valueOf(8000);
                        break;
                    default:
                        baseAmount = BigDecimal.valueOf(1500 + random.nextInt(2000));
                }

                int day = 1 + random.nextInt(Math.min(28, monthDate.lengthOfMonth()));
                LocalDate expDate = LocalDate.of(year, month, day);

                demoList.add(Expense.builder()
                        .amount(baseAmount)
                        .expenseDate(expDate)
                        .description(cat.getName() + " for " + expDate.getMonth().name().toLowerCase())
                        .category(cat)
                        .build());
            }

            // Seed Business expenses
            for (Category cat : businessCats) {
                BigDecimal baseAmount;
                switch (cat.getName()) {
                    case "Operations":
                        baseAmount = BigDecimal.valueOf(30000 + random.nextInt(20000));
                        break;
                    case "Marketing":
                        baseAmount = BigDecimal.valueOf(15000 + random.nextInt(15000));
                        break;
                    case "Software Subscriptions":
                        baseAmount = BigDecimal.valueOf(12000 + random.nextInt(4000));
                        break;
                    case "Taxes":
                        // quarterly
                        if (month % 3 == 0) {
                            baseAmount = BigDecimal.valueOf(25000 + random.nextInt(10000));
                        } else {
                            continue;
                        }
                        break;
                    case "Investments":
                        if (random.nextBoolean()) {
                            baseAmount = BigDecimal.valueOf(40000 + random.nextInt(30000));
                        } else {
                            continue;
                        }
                        break;
                    default:
                        baseAmount = BigDecimal.valueOf(5000 + random.nextInt(10000));
                }

                int day = 1 + random.nextInt(Math.min(28, monthDate.lengthOfMonth()));
                LocalDate expDate = LocalDate.of(year, month, day);

                demoList.add(Expense.builder()
                        .amount(baseAmount)
                        .expenseDate(expDate)
                        .description("Business " + cat.getName() + " - " + expDate.getMonth().name().toLowerCase())
                        .category(cat)
                        .build());
            }
        }

        expenseRepository.saveAll(demoList);
        return demoList.size();
    }
}
