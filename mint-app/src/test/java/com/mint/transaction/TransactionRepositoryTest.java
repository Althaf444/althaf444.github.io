package com.mint.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void findByTypeReturnsMatchingTransactions() {
        transactionRepository.save(buildTransaction("Salary", TransactionType.INCOME, "2500.00"));
        transactionRepository.save(buildTransaction("Food", TransactionType.EXPENSE, "20.00"));
        transactionRepository.save(buildTransaction("Gift", TransactionType.INCOME, "100.00"));

        List<Transaction> incomes = transactionRepository.findByType(TransactionType.INCOME);

        assertEquals(2, incomes.size());
        assertEquals(TransactionType.INCOME, incomes.get(0).getType());
    }

    @Test
    void findByCategoryReturnsMatchingTransactions() {
        transactionRepository.save(buildTransaction("Travel", TransactionType.EXPENSE, "40.00"));
        transactionRepository.save(buildTransaction("Food", TransactionType.EXPENSE, "25.00"));
        transactionRepository.save(buildTransaction("Travel", TransactionType.INCOME, "15.00"));

        List<Transaction> travel = transactionRepository.findByCategory("Travel");

        assertEquals(2, travel.size());
        assertEquals("Travel", travel.get(0).getCategory());
    }

    private Transaction buildTransaction(String category, TransactionType type, String amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(amount));
        transaction.setDate(LocalDate.of(2026, 3, 25));
        transaction.setCategory(category);
        transaction.setType(type);
        return transaction;
    }
}

