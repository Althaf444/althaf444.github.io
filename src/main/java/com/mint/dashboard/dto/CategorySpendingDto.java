package com.mint.dashboard.dto;

import java.math.BigDecimal;

/**
 * Per-category spending breakdown used inside AnalyticsResponseDto.
 * Represents one data point for the frontend charts (Workflow C).
 */
public class CategorySpendingDto {

    private String category;
    private BigDecimal totalAmount;
    private long transactionCount;

    private CategorySpendingDto(Builder builder) {
        this.category = builder.category;
        this.totalAmount = builder.totalAmount;
        this.transactionCount = builder.transactionCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String category;
        private BigDecimal totalAmount;
        private long transactionCount;

        public Builder category(String category) { this.category = category; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder transactionCount(long transactionCount) { this.transactionCount = transactionCount; return this; }

        public CategorySpendingDto build() { return new CategorySpendingDto(this); }
    }

    public String getCategory() { return category; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public long getTransactionCount() { return transactionCount; }
}
