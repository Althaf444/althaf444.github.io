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

    public BankTransactionDto() {}

    public BankTransactionDto(String transactionId, String accountId, BigDecimal amount, String currency, 
                              String description, LocalDateTime transactionDate, String status) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.transactionDate = transactionDate;
        this.status = status;
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

