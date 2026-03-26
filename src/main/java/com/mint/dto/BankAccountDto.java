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

    public BankAccountDto() {}

    public BankAccountDto(String accountId, String accountType, String currency, BigDecimal balance, LocalDateTime lastUpdated) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.lastUpdated = lastUpdated;
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

