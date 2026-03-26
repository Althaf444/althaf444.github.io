package com.mint.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void getAllReturnsList() {
        when(transactionService.getAllTransactions()).thenReturn(List.of(buildTransaction(1L, "Food", TransactionType.EXPENSE)));

        List<Transaction> result = transactionController.getAll();

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategory());
    }

    @Test
    void getByIdReturnsOkResponse() {
        when(transactionService.getTransactionById(1L)).thenReturn(buildTransaction(1L, "Salary", TransactionType.INCOME));

        var response = transactionController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void createReturnsOkResponse() {
        Transaction payload = buildTransaction(null, "Bonus", TransactionType.INCOME);
        Transaction saved = buildTransaction(9L, "Bonus", TransactionType.INCOME);
        when(transactionService.createTransaction(payload)).thenReturn(saved);

        var response = transactionController.create(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(9L, response.getBody().getId());
    }

    @Test
    void updateReturnsOkResponse() {
        Transaction payload = buildTransaction(null, "Groceries", TransactionType.EXPENSE);
        Transaction updated = buildTransaction(5L, "Groceries", TransactionType.EXPENSE);
        when(transactionService.updateTransaction(5L, payload)).thenReturn(updated);

        var response = transactionController.update(5L, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Groceries", response.getBody().getCategory());
    }

    @Test
    void deleteReturnsNoContentResponse() {
        doNothing().when(transactionService).deleteTransaction(3L);

        var response = transactionController.delete(3L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService).deleteTransaction(3L);
    }

    @Test
    void getByTypeReturnsList() {
        when(transactionService.getByType(TransactionType.EXPENSE))
                .thenReturn(List.of(buildTransaction(1L, "Food", TransactionType.EXPENSE)));

        List<Transaction> result = transactionController.getByType(TransactionType.EXPENSE);

        assertEquals(1, result.size());
        assertEquals(TransactionType.EXPENSE, result.get(0).getType());
    }

    @Test
    void getByCategoryReturnsList() {
        when(transactionService.getByCategory("Travel"))
                .thenReturn(List.of(buildTransaction(2L, "Travel", TransactionType.EXPENSE)));

        List<Transaction> result = transactionController.getByCategory("Travel");

        assertEquals(1, result.size());
        assertEquals("Travel", result.get(0).getCategory());
    }

    private Transaction buildTransaction(Long id, String category, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(new BigDecimal("10.00"));
        transaction.setDate(LocalDate.of(2026, 3, 25));
        transaction.setCategory(category);
        transaction.setType(type);
        return transaction;
    }
}

