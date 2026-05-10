package com.mint.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getPercentUsed() {
        return percentUsed;
    }

    public void setPercentUsed(BigDecimal percentUsed) {
        this.percentUsed = percentUsed;
    }

    public boolean isOverBudget() {
        return overBudget;
    }

    public void setOverBudget(boolean overBudget) {
        this.overBudget = overBudget;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long budgetId;
        private String category;
        private BigDecimal monthlyLimit;
        private BigDecimal spentAmount;
        private BigDecimal remainingAmount;
        private BigDecimal percentUsed;
        private boolean overBudget;
        private LocalDate periodStart;
        private LocalDate periodEnd;

        public Builder budgetId(Long budgetId) {
            this.budgetId = budgetId;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder monthlyLimit(BigDecimal monthlyLimit) {
            this.monthlyLimit = monthlyLimit;
            return this;
        }

        public Builder spentAmount(BigDecimal spentAmount) {
            this.spentAmount = spentAmount;
            return this;
        }

        public Builder remainingAmount(BigDecimal remainingAmount) {
            this.remainingAmount = remainingAmount;
            return this;
        }

        public Builder percentUsed(BigDecimal percentUsed) {
            this.percentUsed = percentUsed;
            return this;
        }

        public Builder overBudget(boolean overBudget) {
            this.overBudget = overBudget;
            return this;
        }

        public Builder periodStart(LocalDate periodStart) {
            this.periodStart = periodStart;
            return this;
        }

        public Builder periodEnd(LocalDate periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }

        public BudgetCategoryStatusDto build() {
            BudgetCategoryStatusDto dto = new BudgetCategoryStatusDto();
            dto.budgetId = this.budgetId;
            dto.category = this.category;
            dto.monthlyLimit = this.monthlyLimit;
            dto.spentAmount = this.spentAmount;
            dto.remainingAmount = this.remainingAmount;
            dto.percentUsed = this.percentUsed;
            dto.overBudget = this.overBudget;
            dto.periodStart = this.periodStart;
            dto.periodEnd = this.periodEnd;
            return dto;
        }
    }
}
