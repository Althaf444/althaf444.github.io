package com.mint.transaction.dto;

import com.mint.transaction.Transaction;

/**
 * Result of creating or updating a transaction, including an optional budget warning for expenses.
 */
public class TransactionSaveResponseDto {

    private Transaction transaction;
    /** Present when the expense pushes its category over the monthly budget for that calendar month. */
    private String budgetWarning;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getBudgetWarning() {
        return budgetWarning;
    }

    public void setBudgetWarning(String budgetWarning) {
        this.budgetWarning = budgetWarning;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Transaction transaction;
        private String budgetWarning;

        public Builder transaction(Transaction transaction) {
            this.transaction = transaction;
            return this;
        }

        public Builder budgetWarning(String budgetWarning) {
            this.budgetWarning = budgetWarning;
            return this;
        }

        public TransactionSaveResponseDto build() {
            TransactionSaveResponseDto dto = new TransactionSaveResponseDto();
            dto.transaction = this.transaction;
            dto.budgetWarning = this.budgetWarning;
            return dto;
        }
    }
}
