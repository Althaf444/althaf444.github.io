package com.mint.budget.create;

import com.mint.budget.dto.BudgetRequestDto;
import com.mint.budget.dto.BudgetResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
public class BudgetCreateController {

    private final BudgetCreateService budgetCreateService;

    public BudgetCreateController(BudgetCreateService budgetCreateService) {
        this.budgetCreateService = budgetCreateService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BudgetRequestDto body) {
        try {
            BudgetResponseDto created = budgetCreateService.create(body);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
