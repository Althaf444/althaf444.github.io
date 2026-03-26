package com.mint.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction updated) {
        Transaction existing = getTransactionById(id);
        existing.setAmount(updated.getAmount());
        existing.setDate(updated.getDate());
        existing.setCategory(updated.getCategory());
        existing.setType(updated.getType());
        return transactionRepository.save(existing);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }

    public List<Transaction> getByCategory(String category) {
        return transactionRepository.findByCategory(category);
    }
}