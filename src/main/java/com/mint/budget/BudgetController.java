package com.mint.budget;

import com.mint.budget.dto.BudgetOverviewDto;
import com.mint.budget.dto.BudgetRequestDto;
import com.mint.budget.dto.BudgetResponseDto;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.util.List;

/**
 * REST API for category budgets (monthly limits) and progress vs actual spending.
 */
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<BudgetResponseDto> list() {
        return budgetService.listBudgets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(budgetService.getBudget(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BudgetRequestDto body) {
        try {
            return ResponseEntity.ok(budgetService.createBudget(body));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BudgetRequestDto body) {
        try {
            return ResponseEntity.ok(budgetService.updateBudget(id, body));
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("Budget not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Monthly roll-up: budgeted amount vs expenses in that calendar month, per category.
     *
     * GET /api/budgets/overview?year=2026&amp;month=4
     */
    @GetMapping("/overview")
    public ResponseEntity<BudgetOverviewDto> overview(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            return ResponseEntity.ok(budgetService.getMonthlyOverview(year, month));
        } catch (DateTimeException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
