package com.mint.budget;

import com.mint.budget.dto.BudgetCategoryStatusDto;
import com.mint.budget.dto.BudgetOverviewDto;
import com.mint.budget.dto.BudgetRequestDto;
import com.mint.budget.dto.BudgetResponseDto;
import com.mint.transaction.Transaction;
import com.mint.transaction.TransactionRepository;
import com.mint.transaction.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<BudgetResponseDto> listBudgets() {
        return budgetRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BudgetResponseDto getBudget(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public BudgetResponseDto createBudget(BudgetRequestDto request) {
        String category = normalizeCategory(request.getCategory());
        budgetRepository.findByCategoryIgnoreCase(category).ifPresent(b -> {
            throw new IllegalArgumentException("A budget already exists for category: " + category);
        });
        Budget saved = budgetRepository.save(Budget.builder()
                .category(category)
                .monthlyLimit(request.getMonthlyLimit())
                .build());
        return toResponse(saved);
    }

    @Transactional
    public BudgetResponseDto updateBudget(Long id, BudgetRequestDto request) {
        Budget existing = findById(id);
        String category = normalizeCategory(request.getCategory());
        if (!existing.getCategory().equalsIgnoreCase(category)
                && budgetRepository.existsByCategoryIgnoreCaseAndIdNot(category, id)) {
            throw new IllegalArgumentException("A budget already exists for category: " + category);
        }
        existing.setCategory(category);
        existing.setMonthlyLimit(request.getMonthlyLimit());
        return toResponse(budgetRepository.save(existing));
    }

    @Transactional
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new IllegalArgumentException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }

    /**
     * Compares each category budget to actual expense transactions in the given calendar month.
     */
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
                            .percentUsed(percentUsed(spent, limit))
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

    /**
     * After an expense is saved, checks whether total spending in that category for the transaction's
     * calendar month exceeds the configured monthly limit.
     */
    public Optional<String> buildOverBudgetWarningIfAny(Transaction expense) {
        if (expense == null || expense.getType() != TransactionType.EXPENSE) {
            return Optional.empty();
        }
        if (expense.getCategory() == null || expense.getCategory().isBlank()) {
            return Optional.empty();
        }
        Optional<Budget> budgetOpt = budgetRepository.findByCategoryIgnoreCase(expense.getCategory().trim());
        if (budgetOpt.isEmpty()) {
            return Optional.empty();
        }
        Budget budget = budgetOpt.get();
        YearMonth ym = YearMonth.from(expense.getDate());
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();
        List<Transaction> monthExpenses = transactionRepository.findByTypeAndDateBetween(
                TransactionType.EXPENSE, from, to);
        String canonical = budget.getCategory();
        BigDecimal total = monthExpenses.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().trim().equalsIgnoreCase(canonical))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(budget.getMonthlyLimit()) > 0) {
            String period = expense.getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
            return Optional.of(String.format(
                    "Over budget for \"%s\" in %s: spent %s vs limit %s.",
                    canonical, period, total.toPlainString(),
                    budget.getMonthlyLimit().toPlainString()));
        }
        return Optional.empty();
    }

    private Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
    }

    private BudgetResponseDto toResponse(Budget b) {
        return BudgetResponseDto.builder()
                .id(b.getId())
                .category(b.getCategory())
                .monthlyLimit(b.getMonthlyLimit())
                .build();
    }

    private static String normalizeCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        String trimmed = category.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        return trimmed;
    }

    private static BigDecimal percentUsed(BigDecimal spent, BigDecimal limit) {
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return spent.compareTo(BigDecimal.ZERO) > 0
                    ? new BigDecimal("100.00")
                    : BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        }
        return spent.multiply(new BigDecimal("100"))
                .divide(limit, 2, RoundingMode.HALF_UP);
    }
}
