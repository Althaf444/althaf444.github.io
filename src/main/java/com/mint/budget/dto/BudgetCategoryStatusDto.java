package com.mint.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCategoryStatusDto {

    private Long budgetId;
    private String category;
    private BigDecimal monthlyLimit;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    /** Spending as a percentage of the monthly limit (can exceed 100 when over budget). */
    private BigDecimal percentUsed;
    private boolean overBudget;
    private LocalDate periodStart;
    private LocalDate periodEnd;
}
