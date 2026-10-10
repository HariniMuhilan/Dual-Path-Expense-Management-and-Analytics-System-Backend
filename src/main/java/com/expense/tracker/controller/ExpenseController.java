package com.expense.tracker.controller;

import com.expense.tracker.dto.ExpenseDto;
import com.expense.tracker.entity.Expense;
import com.expense.tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody ExpenseDto expenseDto) {
        return ResponseEntity.ok(expenseService.addExpense(expenseDto));
    }

    @GetMapping
    public ResponseEntity<Page<Expense>> getAllExpenses(Pageable pageable) {
        return ResponseEntity.ok(expenseService.getAllExpenses(pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Expense>> getExpensesByFilter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(expenseService.getExpensesByFilter(startDate, endDate, mode));
    }

    @PostMapping("/seed-demo")
    public ResponseEntity<Map<String, Object>> seedDemoData() {
        int count = expenseService.seedDemoExpenses();
        return ResponseEntity.ok(Map.of("message", "Demo data seeded successfully", "count", count));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody ExpenseDto expenseDto) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expenseDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
