package com.mint.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for bank transaction retrieved from bank API
 */
public class BankTransactionDto {
    private String transactionId;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime transactionDate;
    private String status; // PENDING, COMPLETED, FAILED

    // Required for frameworks (e.g. Jackson deserialization)
    protected BankTransactionDto() {}

    private BankTransactionDto(Builder builder) {
        this.transactionId = builder.transactionId;
        this.accountId = builder.accountId;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.description = builder.description;
        this.transactionDate = builder.transactionDate;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String transactionId;
        private String accountId;
        private BigDecimal amount;
        private String currency;
        private String description;
        private LocalDateTime transactionDate;
        private String status;

        public Builder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public Builder accountId(String accountId) { this.accountId = accountId; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder transactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public BankTransactionDto build() { return new BankTransactionDto(this); }
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
