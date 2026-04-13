package com.mint.dashboard;

import com.mint.dashboard.dto.AnalyticsResponseDto;
import com.mint.dashboard.dto.CategorySpendingDto;
import com.mint.dashboard.dto.DashboardSummaryDto;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.repository.BankConnectionRepository;
import com.mint.transaction.Transaction;
import com.mint.transaction.TransactionRepository;
import com.mint.transaction.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for the Dashboard module.
 *
 * Integrates data from two modules:
 *   - Transaction module  : Workflow A (balance) and Workflow C (analytics charts)
 *   - Bank Connection module : linked account count and last sync date in the summary
 */
@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final BankConnectionRepository bankConnectionRepository;

    public DashboardService(TransactionRepository transactionRepository,
                            BankConnectionRepository bankConnectionRepository) {
        this.transactionRepository = transactionRepository;
        this.bankConnectionRepository = bankConnectionRepository;
    }

    /**
     * Returns an overall financial summary (Workflow A).
     *
     * Transaction data:
     *   - Total income, total expense, net balance, transaction count
     *
     * Bank Connection data:
     *   - Number of accounts with LINKED status
     *   - Most recent successful sync date across all linked accounts
     */
    public DashboardSummaryDto getSummary() {
        // --- Transaction aggregation ---
        List<Transaction> all = transactionRepository.findAll();

        BigDecimal totalIncome = all.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = all.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- Bank Connection aggregation ---
        List<BankConnection> linkedAccounts = bankConnectionRepository.findByStatus(ConnectionStatus.LINKED);

        LocalDateTime lastBankSyncDate = linkedAccounts.stream()
                .map(BankConnection::getLastSyncDate)
                .filter(date -> date != null)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return DashboardSummaryDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .totalBalance(totalIncome.subtract(totalExpense))
                .transactionCount(all.size())
                .linkedBankAccounts(linkedAccounts.size())
                .lastBankSyncDate(lastBankSyncDate)
                .build();
    }

    /**
     * Returns spending aggregated by category for a given date range (Workflow C).
     * Only EXPENSE transactions are counted — this data populates frontend charts.
     *
     * @param from start date (inclusive)
     * @param to   end date (inclusive)
     */
    public AnalyticsResponseDto getAnalytics(LocalDate from, LocalDate to) {
        List<Transaction> expenses = transactionRepository
                .findByTypeAndDateBetween(TransactionType.EXPENSE, from, to);

        Map<String, List<Transaction>> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory));

        List<CategorySpendingDto> categories = byCategory.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return CategorySpendingDto.builder()
                            .category(entry.getKey())
                            .totalAmount(total)
                            .transactionCount(entry.getValue().size())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalSpending = categories.stream()
                .map(CategorySpendingDto::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AnalyticsResponseDto.builder()
                .from(from)
                .to(to)
                .totalSpending(totalSpending)
                .categories(categories)
                .build();
    }
}
