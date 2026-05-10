package com.mint.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Overall financial summary returned by GET /api/dashboard/summary.
 *
 * Combines data from two modules:
 *   - Transaction module: total income, total expense, net balance (Workflow A)
 *   - Bank Connection module: linked account count and last successful sync date
 */
public class DashboardSummaryDto {

    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private long transactionCount;

    // Bank Connection & Sync integration
    private int linkedBankAccounts;
    private LocalDateTime lastBankSyncDate;

    private DashboardSummaryDto(Builder builder) {
        this.totalBalance = builder.totalBalance;
        this.totalIncome = builder.totalIncome;
        this.totalExpense = builder.totalExpense;
        this.transactionCount = builder.transactionCount;
        this.linkedBankAccounts = builder.linkedBankAccounts;
        this.lastBankSyncDate = builder.lastBankSyncDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal totalBalance;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private long transactionCount;
        private int linkedBankAccounts;
        private LocalDateTime lastBankSyncDate;

        public Builder totalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; return this; }
        public Builder totalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; return this; }
        public Builder totalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; return this; }
        public Builder transactionCount(long transactionCount) { this.transactionCount = transactionCount; return this; }
        public Builder linkedBankAccounts(int linkedBankAccounts) { this.linkedBankAccounts = linkedBankAccounts; return this; }
        public Builder lastBankSyncDate(LocalDateTime lastBankSyncDate) { this.lastBankSyncDate = lastBankSyncDate; return this; }

        public DashboardSummaryDto build() { return new DashboardSummaryDto(this); }
    }

    public BigDecimal getTotalBalance() { return totalBalance; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public long getTransactionCount() { return transactionCount; }
    public int getLinkedBankAccounts() { return linkedBankAccounts; }
    public LocalDateTime getLastBankSyncDate() { return lastBankSyncDate; }
}
