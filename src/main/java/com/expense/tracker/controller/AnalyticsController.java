package com.expense.tracker.controller;

import com.expense.tracker.dto.CategoryExpenseSummaryDto;
import com.expense.tracker.dto.ComparisonReportDto;
import com.expense.tracker.dto.TimeSeriesSummaryDto;
import com.expense.tracker.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/weekly")
    public ResponseEntity<List<CategoryExpenseSummaryDto>> getWeeklyAnalytics(
            @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(analyticsService.getWeeklyAnalytics(mode));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<CategoryExpenseSummaryDto>> getMonthlyAnalytics(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(analyticsService.getMonthlyAnalytics(year, month, mode));
    }

    @GetMapping("/yearly")
    public ResponseEntity<List<CategoryExpenseSummaryDto>> getYearlyAnalytics(
            @RequestParam int year,
            @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(analyticsService.getYearlyAnalytics(year, mode));
    }

    @GetMapping("/range")
    public ResponseEntity<List<CategoryExpenseSummaryDto>> getRangeAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(analyticsService.getRangeAnalytics(startDate, endDate, mode));
    }

    @GetMapping("/timeseries")
    public ResponseEntity<List<TimeSeriesSummaryDto>> getTimeSeriesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(analyticsService.getTimeSeriesAnalytics(startDate, endDate, mode));
    }

    @GetMapping("/compare")
    public ResponseEntity<ComparisonReportDto> getComparisonAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start2,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end2,
            @RequestParam(required = false) String mode) {
        return ResponseEntity.ok(analyticsService.getComparisonAnalytics(start1, end1, start2, end2, mode));
    }
}
