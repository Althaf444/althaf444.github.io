package com.mint.dashboard;

import com.mint.dashboard.dto.AnalyticsResponseDto;
import com.mint.dashboard.dto.DashboardSummaryDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for the Dashboard module.
 *
 * Endpoints:
 *   GET /api/dashboard/summary    — overall financial summary (Workflow A)
 *   GET /api/dashboard/analytics  — per-category spending for a date range (Workflow C)
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Returns total balance, total income, total expense, and transaction count.
     *
     * GET /api/dashboard/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    /**
     * Returns spending aggregated by category for the given date range.
     * Used to populate charts on the Reports tab (Workflow C).
     *
     * GET /api/dashboard/analytics?from=2025-01-01&to=2025-01-31
     *
     * @param from start date (inclusive), format: yyyy-MM-dd
     * @param to   end date (inclusive), format: yyyy-MM-dd
     */
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsResponseDto> getAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(dashboardService.getAnalytics(from, to));
    }
}
