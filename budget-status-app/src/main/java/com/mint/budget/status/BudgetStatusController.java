package com.mint.budget.status;

import com.mint.budget.dto.BudgetOverviewDto;
import com.mint.budget.dto.BudgetResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DateTimeException;
import java.util.List;

/**
 * Read-only API for budgets: list, detail, and monthly status vs spending.
 */
@RestController
@RequestMapping("/api/budgets")
public class BudgetStatusController {

    private final BudgetStatusService budgetStatusService;

    public BudgetStatusController(BudgetStatusService budgetStatusService) {
        this.budgetStatusService = budgetStatusService;
    }

    @GetMapping
    public List<BudgetResponseDto> list() {
        return budgetStatusService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(budgetStatusService.get(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/budgets/overview?year=2026&amp;month=4
     */
    @GetMapping("/overview")
    public ResponseEntity<BudgetOverviewDto> overview(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            return ResponseEntity.ok(budgetStatusService.getMonthlyOverview(year, month));
        } catch (DateTimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
