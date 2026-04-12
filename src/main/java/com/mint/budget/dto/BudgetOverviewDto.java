package com.mint.budget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetOverviewDto {

    private int year;
    private int month;
    private BigDecimal totalBudgeted;
    private BigDecimal totalSpent;
    private BigDecimal totalRemaining;
    private List<BudgetCategoryStatusDto> categories;
}
