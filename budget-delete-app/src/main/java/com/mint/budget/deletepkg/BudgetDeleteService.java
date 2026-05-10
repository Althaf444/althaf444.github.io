package com.mint.budget.deletepkg;

import com.mint.budget.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetDeleteService {

    private final BudgetRepository budgetRepository;

    public BudgetDeleteService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public void delete(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new IllegalArgumentException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }
}
