package com.mint.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getAllTransactionsReturnsAllItems() {
        List<Transaction> expected = List.of(buildTransaction(1L, "Food", TransactionType.EXPENSE));
        when(transactionRepository.findAll()).thenReturn(expected);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategory());
    }

    @Test
    void getTransactionByIdReturnsItemWhenFound() {
        Transaction transaction = buildTransaction(1L, "Salary", TransactionType.INCOME);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(1L);

        assertEquals(1L, result.getId());
        assertEquals(TransactionType.INCOME, result.getType());
    }

    @Test
    void getTransactionByIdThrowsWhenMissing() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.getTransactionById(99L));

        assertEquals("Transaction not found with id: 99", exception.getMessage());
    }

    @Test
    void createTransactionPersistsData() {
        Transaction transaction = buildTransaction(null, "Bonus", TransactionType.INCOME);
        Transaction saved = buildTransaction(10L, "Bonus", TransactionType.INCOME);
        when(transactionRepository.save(transaction)).thenReturn(saved);

        Transaction result = transactionService.createTransaction(transaction);

        assertEquals(10L, result.getId());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void updateTransactionCopiesFieldsAndSaves() {
        Transaction existing = buildTransaction(5L, "Old", TransactionType.EXPENSE);
        Transaction updatePayload = buildTransaction(null, "NewCategory", TransactionType.INCOME);
        updatePayload.setAmount(new BigDecimal("99.99"));
        updatePayload.setDate(LocalDate.of(2026, 3, 25));

        when(transactionRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction updated = transactionService.updateTransaction(5L, updatePayload);

        assertEquals(5L, updated.getId());
        assertEquals(new BigDecimal("99.99"), updated.getAmount());
        assertEquals(LocalDate.of(2026, 3, 25), updated.getDate());
        assertEquals("NewCategory", updated.getCategory());
        assertEquals(TransactionType.INCOME, updated.getType());
    }

    @Test
    void deleteTransactionDelegatesToRepository() {
        transactionService.deleteTransaction(3L);

        verify(transactionRepository, times(1)).deleteById(3L);
    }

    @Test
    void getByTypeDelegatesToRepository() {
        List<Transaction> expected = List.of(buildTransaction(1L, "Food", TransactionType.EXPENSE));
        when(transactionRepository.findByType(TransactionType.EXPENSE)).thenReturn(expected);

        List<Transaction> result = transactionService.getByType(TransactionType.EXPENSE);

        assertEquals(1, result.size());
        assertEquals(TransactionType.EXPENSE, result.get(0).getType());
    }

    @Test
    void getByCategoryDelegatesToRepository() {
        List<Transaction> expected = List.of(buildTransaction(1L, "Travel", TransactionType.EXPENSE));
        when(transactionRepository.findByCategory("Travel")).thenReturn(expected);

        List<Transaction> result = transactionService.getByCategory("Travel");

        assertEquals(1, result.size());
        assertEquals("Travel", result.get(0).getCategory());
    }

    private Transaction buildTransaction(Long id, String category, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(new BigDecimal("10.50"));
        transaction.setDate(LocalDate.of(2026, 3, 24));
        transaction.setCategory(category);
        transaction.setType(type);
        return transaction;
    }
}

