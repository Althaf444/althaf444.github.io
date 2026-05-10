package com.mint.budget.edit;

import com.mint.budget.Budget;
import com.mint.budget.BudgetRepository;
import com.mint.budget.dto.BudgetRequestDto;
import com.mint.budget.dto.BudgetResponseDto;
import com.mint.budget.support.BudgetSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetEditService {

    private final BudgetRepository budgetRepository;

    public BudgetEditService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public BudgetResponseDto update(Long id, BudgetRequestDto request) {
        Budget existing = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
        String category = BudgetSupport.normalizeCategory(request.getCategory());
        if (!existing.getCategory().equalsIgnoreCase(category)
                && budgetRepository.existsByCategoryIgnoreCaseAndIdNot(category, id)) {
            throw new IllegalArgumentException("A budget already exists for category: " + category);
        }
        existing.setCategory(category);
        existing.setMonthlyLimit(request.getMonthlyLimit());
        return BudgetSupport.toResponse(budgetRepository.save(existing));
    }
}
