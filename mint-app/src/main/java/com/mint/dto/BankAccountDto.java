package com.mint.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for bank account information retrieved from bank API
 */
public class BankAccountDto {
    private String accountId;
    private String accountType;
    private String currency;
    private BigDecimal balance;
    private LocalDateTime lastUpdated;

    // Required for frameworks (e.g. Jackson deserialization)
    protected BankAccountDto() {}

    private BankAccountDto(Builder builder) {
        this.accountId = builder.accountId;
        this.accountType = builder.accountType;
        this.currency = builder.currency;
        this.balance = builder.balance;
        this.lastUpdated = builder.lastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String accountId;
        private String accountType;
        private String currency;
        private BigDecimal balance;
        private LocalDateTime lastUpdated;

        public Builder accountId(String accountId) { this.accountId = accountId; return this; }
        public Builder accountType(String accountType) { this.accountType = accountType; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder balance(BigDecimal balance) { this.balance = balance; return this; }
        public Builder lastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; return this; }

        public BankAccountDto build() { return new BankAccountDto(this); }
    }

    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
