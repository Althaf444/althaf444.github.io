package com.mint.transaction.dto;

import com.mint.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of creating or updating a transaction, including an optional budget warning for expenses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSaveResponseDto {

    private Transaction transaction;
    /** Present when the expense pushes its category over the monthly budget for that calendar month. */
    private String budgetWarning;
}
