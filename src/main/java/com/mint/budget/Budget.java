package com.mint.budget;

import javax.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "budgets", uniqueConstraints = @UniqueConstraint(columnNames = "category"))
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String category;

    @Column(nullable = false, precision = 19, scale = 4)
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

        public Budget build() {
            Budget budget = new Budget();
            budget.id = this.id;
            budget.category = this.category;
            budget.monthlyLimit = this.monthlyLimit;
            return budget;
        }
    }
}
