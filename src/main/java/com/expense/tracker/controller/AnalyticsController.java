package com.expense.tracker.controller;

import com.expense.tracker.dto.CategoryExpenseSummaryDto;
import com.expense.tracker.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/weekly")
    public ResponseEntity<List<CategoryExpenseSummaryDto>> getWeeklyAnalytics() {
        return ResponseEntity.ok(analyticsService.getWeeklyAnalytics());
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<CategoryExpenseSummaryDto>> getMonthlyAnalytics(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(analyticsService.getMonthlyAnalytics(year, month));
    }
}
