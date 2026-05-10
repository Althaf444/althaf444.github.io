package com.mint.budget.create;

import com.mint.budget.Budget;
import com.mint.budget.BudgetRepository;
import com.mint.budget.dto.BudgetRequestDto;
import com.mint.budget.dto.BudgetResponseDto;
import com.mint.budget.support.BudgetSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetCreateService {

    private final BudgetRepository budgetRepository;

    public BudgetCreateService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public BudgetResponseDto create(BudgetRequestDto request) {
        String category = BudgetSupport.normalizeCategory(request.getCategory());
        budgetRepository.findByCategoryIgnoreCase(category).ifPresent(b -> {
            throw new IllegalArgumentException("A budget already exists for category: " + category);
        });
        Budget saved = budgetRepository.save(Budget.builder()
                .category(category)
                .monthlyLimit(request.getMonthlyLimit())
                .build());
        return BudgetSupport.toResponse(saved);
    }
}
