package com.mint.budget.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.math.BigDecimal;

public class BudgetRequestDto {

    @NotBlank
    @Size(max = 128)
    private String category;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal monthlyLimit;

    // Getters and setters
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
        private String category;
        private BigDecimal monthlyLimit;

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder monthlyLimit(BigDecimal monthlyLimit) {
            this.monthlyLimit = monthlyLimit;
            return this;
        }

        public BudgetRequestDto build() {
            BudgetRequestDto dto = new BudgetRequestDto();
            dto.category = this.category;
            dto.monthlyLimit = this.monthlyLimit;
            return dto;
        }
    }
}
