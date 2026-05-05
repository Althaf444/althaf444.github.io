package com.mint.budget.dto;

import java.math.BigDecimal;
import java.util.List;

public class BudgetOverviewDto {

    private int year;
    private int month;
    private BigDecimal totalBudgeted;
    private BigDecimal totalSpent;
    private BigDecimal totalRemaining;
    private List<BudgetCategoryStatusDto> categories;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getTotalBudgeted() {
        return totalBudgeted;
    }

    public void setTotalBudgeted(BigDecimal totalBudgeted) {
        this.totalBudgeted = totalBudgeted;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getTotalRemaining() {
        return totalRemaining;
    }

    public void setTotalRemaining(BigDecimal totalRemaining) {
        this.totalRemaining = totalRemaining;
    }

    public List<BudgetCategoryStatusDto> getCategories() {
        return categories;
    }

    public void setCategories(List<BudgetCategoryStatusDto> categories) {
        this.categories = categories;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int year;
        private int month;
        private BigDecimal totalBudgeted;
        private BigDecimal totalSpent;
        private BigDecimal totalRemaining;
        private List<BudgetCategoryStatusDto> categories;

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder month(int month) {
            this.month = month;
            return this;
        }

        public Builder totalBudgeted(BigDecimal totalBudgeted) {
            this.totalBudgeted = totalBudgeted;
            return this;
        }

        public Builder totalSpent(BigDecimal totalSpent) {
            this.totalSpent = totalSpent;
            return this;
        }

        public Builder totalRemaining(BigDecimal totalRemaining) {
            this.totalRemaining = totalRemaining;
            return this;
        }

        public Builder categories(List<BudgetCategoryStatusDto> categories) {
            this.categories = categories;
            return this;
        }

        public BudgetOverviewDto build() {
            BudgetOverviewDto dto = new BudgetOverviewDto();
            dto.year = this.year;
            dto.month = this.month;
            dto.totalBudgeted = this.totalBudgeted;
            dto.totalSpent = this.totalSpent;
            dto.totalRemaining = this.totalRemaining;
            dto.categories = this.categories;
            return dto;
        }
    }
}
