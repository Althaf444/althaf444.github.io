package com.mint.budget.edit;

import com.mint.budget.dto.BudgetRequestDto;
import com.mint.budget.dto.BudgetResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
public class BudgetEditController {

    private final BudgetEditService budgetEditService;

    public BudgetEditController(BudgetEditService budgetEditService) {
        this.budgetEditService = budgetEditService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BudgetRequestDto body) {
        try {
            BudgetResponseDto updated = budgetEditService.update(id, body);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("Budget not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
