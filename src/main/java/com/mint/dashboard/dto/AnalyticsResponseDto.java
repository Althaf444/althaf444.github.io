package com.mint.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Analytics response for GET /api/dashboard/analytics.
 * Implements Workflow C from the Dashboard BPMN:
 * fetches transactions for a selected date range and returns
 * aggregated spending per category to populate frontend charts.
 */
public class AnalyticsResponseDto {

    private LocalDate from;
    private LocalDate to;
    private BigDecimal totalSpending;
    private List<CategorySpendingDto> categories;

    private AnalyticsResponseDto(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.totalSpending = builder.totalSpending;
        this.categories = builder.categories;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate from;
        private LocalDate to;
        private BigDecimal totalSpending;
        private List<CategorySpendingDto> categories;

        public Builder from(LocalDate from) { this.from = from; return this; }
        public Builder to(LocalDate to) { this.to = to; return this; }
        public Builder totalSpending(BigDecimal totalSpending) { this.totalSpending = totalSpending; return this; }
        public Builder categories(List<CategorySpendingDto> categories) { this.categories = categories; return this; }

        public AnalyticsResponseDto build() { return new AnalyticsResponseDto(this); }
    }

    public LocalDate getFrom() { return from; }
    public LocalDate getTo() { return to; }
    public BigDecimal getTotalSpending() { return totalSpending; }
    public List<CategorySpendingDto> getCategories() { return categories; }
}
