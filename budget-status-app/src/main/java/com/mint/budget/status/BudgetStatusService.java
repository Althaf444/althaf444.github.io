package com.mint.budget.status;

import com.mint.budget.Budget;
import com.mint.budget.BudgetRepository;
import com.mint.budget.dto.BudgetCategoryStatusDto;
import com.mint.budget.dto.BudgetOverviewDto;
import com.mint.budget.dto.BudgetResponseDto;
import com.mint.budget.support.BudgetSupport;
import com.mint.transaction.Transaction;
import com.mint.transaction.TransactionRepository;
import com.mint.transaction.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetStatusService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetStatusService(BudgetRepository budgetRepository,
                               TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<BudgetResponseDto> list() {
        return budgetRepository.findAll().stream()
                .map(BudgetSupport::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BudgetResponseDto get(Long id) {
        Budget b = budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
        return BudgetSupport.toResponse(b);
    }

    /**
     * Monthly roll-up: budgeted amount vs expenses in that calendar month, per category.
     */
    @Transactional(readOnly = true)
    public BudgetOverviewDto getMonthlyOverview(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<Transaction> expenses = transactionRepository
                .findByTypeAndDateBetween(TransactionType.EXPENSE, from, to);

        Map<String, BigDecimal> spentByCategory = expenses.stream()
                .filter(t -> t.getCategory() != null && !t.getCategory().isBlank())
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().trim().toLowerCase(Locale.ROOT),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        List<Budget> budgets = budgetRepository.findAll();

        BigDecimal totalBudgeted = budgets.stream()
                .map(Budget::getMonthlyLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BudgetCategoryStatusDto> items = budgets.stream()
                .map(b -> {
                    BigDecimal spent = spentByCategory.getOrDefault(
                            b.getCategory().toLowerCase(Locale.ROOT), BigDecimal.ZERO);
                    BigDecimal limit = b.getMonthlyLimit();
                    BigDecimal remaining = limit.subtract(spent);
                    boolean over = spent.compareTo(limit) > 0;
                    return BudgetCategoryStatusDto.builder()
                            .budgetId(b.getId())
                            .category(b.getCategory())
                            .monthlyLimit(limit)
                            .spentAmount(spent)
                            .remainingAmount(remaining)
                            .percentUsed(BudgetSupport.percentUsed(spent, limit))
                            .overBudget(over)
                            .periodStart(from)
                            .periodEnd(to)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal totalSpent = items.stream()
                .map(BudgetCategoryStatusDto::getSpentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BudgetOverviewDto.builder()
                .year(year)
                .month(month)
                .totalBudgeted(totalBudgeted)
                .totalSpent(totalSpent)
                .totalRemaining(totalBudgeted.subtract(totalSpent))
                .categories(items)
                .build();
    }
}
