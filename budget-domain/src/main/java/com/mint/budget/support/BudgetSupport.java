package com.mint.budget.support;

import com.mint.budget.Budget;
import com.mint.budget.dto.BudgetResponseDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Shared helpers for budget CRUD and overview calculations.
 */
public final class BudgetSupport {

    private BudgetSupport() {
    }

    public static String normalizeCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        String trimmed = category.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        return trimmed;
    }

    public static BudgetResponseDto toResponse(Budget b) {
        return BudgetResponseDto.builder()
                .id(b.getId())
                .category(b.getCategory())
                .monthlyLimit(b.getMonthlyLimit())
                .build();
    }

    public static BigDecimal percentUsed(BigDecimal spent, BigDecimal limit) {
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return spent.compareTo(BigDecimal.ZERO) > 0
                    ? new BigDecimal("100.00")
                    : BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        }
        return spent.multiply(new BigDecimal("100"))
                .divide(limit, 2, RoundingMode.HALF_UP);
    }
}
