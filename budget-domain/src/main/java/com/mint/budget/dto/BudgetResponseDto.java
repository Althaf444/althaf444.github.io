package com.mint.budget.dto;

import java.math.BigDecimal;

public class BudgetResponseDto {

    private Long id;
    private String category;
    private BigDecimal monthlyLimit;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String category;
        private BigDecimal monthlyLimit;

        public Builder id(Long id) {
            this.id = id;
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

        public BudgetResponseDto build() {
            BudgetResponseDto dto = new BudgetResponseDto();
            dto.id = this.id;
            dto.category = this.category;
            dto.monthlyLimit = this.monthlyLimit;
            return dto;
        }
    }
}
