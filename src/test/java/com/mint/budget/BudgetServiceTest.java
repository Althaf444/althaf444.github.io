package com.mint.budget;

import com.mint.budget.dto.BudgetOverviewDto;
import com.mint.budget.dto.BudgetRequestDto;
import com.mint.transaction.Transaction;
import com.mint.transaction.TransactionRepository;
import com.mint.transaction.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void getMonthlyOverview_aggregatesExpensesByCategory() {
        Budget food = Budget.builder().id(1L).category("Food").monthlyLimit(new BigDecimal("300.00")).build();
        when(budgetRepository.findAll()).thenReturn(List.of(food));

        List<Transaction> expenses = List.of(
                Transaction.builder()
                        .amount(new BigDecimal("50.00"))
                        .date(LocalDate.of(2026, 4, 10))
                        .category("Food")
                        .type(TransactionType.EXPENSE)
                        .build(),
                Transaction.builder()
                        .amount(new BigDecimal("75.50"))
                        .date(LocalDate.of(2026, 4, 15))
                        .category("Food")
                        .type(TransactionType.EXPENSE)
                        .build()
        );
        when(transactionRepository.findByTypeAndDateBetween(
                TransactionType.EXPENSE,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)))
                .thenReturn(expenses);

        BudgetOverviewDto overview = budgetService.getMonthlyOverview(2026, 4);

        assertThat(overview.getTotalBudgeted()).isEqualByComparingTo("300.00");
        assertThat(overview.getTotalSpent()).isEqualByComparingTo("125.50");
        assertThat(overview.getTotalRemaining()).isEqualByComparingTo("174.50");
        assertThat(overview.getCategories()).hasSize(1);
        assertThat(overview.getCategories().get(0).getSpentAmount()).isEqualByComparingTo("125.50");
        assertThat(overview.getCategories().get(0).isOverBudget()).isFalse();
        assertThat(overview.getCategories().get(0).getPercentUsed()).isEqualByComparingTo("41.83");
    }

    @Test
    void getMonthlyOverview_overBudget_setsFlagAndPercentOver100() {
        Budget rent = Budget.builder().id(2L).category("Rent").monthlyLimit(new BigDecimal("100.00")).build();
        when(budgetRepository.findAll()).thenReturn(List.of(rent));
        when(transactionRepository.findByTypeAndDateBetween(
                TransactionType.EXPENSE,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)))
                .thenReturn(List.of(
                        Transaction.builder()
                                .amount(new BigDecimal("120.00"))
                                .date(LocalDate.of(2026, 3, 5))
                                .category("Rent")
                                .type(TransactionType.EXPENSE)
                                .build()
                ));

        BudgetOverviewDto overview = budgetService.getMonthlyOverview(2026, 3);

        assertThat(overview.getCategories().get(0).isOverBudget()).isTrue();
        assertThat(overview.getCategories().get(0).getPercentUsed()).isEqualByComparingTo("120.00");
        assertThat(overview.getCategories().get(0).getRemainingAmount()).isEqualByComparingTo("-20.00");
    }

    @Test
    void createBudget_duplicateCategory_throws() {
        when(budgetRepository.findByCategoryIgnoreCase("Food"))
                .thenReturn(Optional.of(Budget.builder().id(1L).category("Food").monthlyLimit(BigDecimal.TEN).build()));

        BudgetRequestDto req = BudgetRequestDto.builder()
                .category("Food")
                .monthlyLimit(new BigDecimal("200.00"))
                .build();

        assertThatThrownBy(() -> budgetService.createBudget(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createBudget_trimsCategory() {
        when(budgetRepository.findByCategoryIgnoreCase("Coffee")).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(inv -> {
                    Budget b = inv.getArgument(0);
                    b.setId(10L);
                    return b;
                });

        BudgetRequestDto req = BudgetRequestDto.builder()
                .category("  Coffee  ")
                .monthlyLimit(new BigDecimal("50.00"))
                .build();

        var created = budgetService.createBudget(req);

        assertThat(created.getCategory()).isEqualTo("Coffee");
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void getMonthlyOverview_matchesSpentCaseInsensitive() {
        Budget food = Budget.builder().id(1L).category("Food").monthlyLimit(new BigDecimal("300.00")).build();
        when(budgetRepository.findAll()).thenReturn(List.of(food));
        when(transactionRepository.findByTypeAndDateBetween(
                eq(TransactionType.EXPENSE),
                eq(LocalDate.of(2026, 4, 1)),
                eq(LocalDate.of(2026, 4, 30))))
                .thenReturn(List.of(
                        Transaction.builder()
                                .amount(new BigDecimal("100.00"))
                                .date(LocalDate.of(2026, 4, 2))
                                .category("food")
                                .type(TransactionType.EXPENSE)
                                .build()
                ));

        BudgetOverviewDto overview = budgetService.getMonthlyOverview(2026, 4);

        assertThat(overview.getCategories().get(0).getSpentAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    void buildOverBudgetWarningIfAny_whenOverLimit_returnsMessage() {
        Budget food = Budget.builder().id(1L).category("Food").monthlyLimit(new BigDecimal("100.00")).build();
        when(budgetRepository.findByCategoryIgnoreCase(anyString())).thenReturn(Optional.of(food));
        when(transactionRepository.findByTypeAndDateBetween(
                TransactionType.EXPENSE,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)))
                .thenReturn(List.of(
                        Transaction.builder()
                                .amount(new BigDecimal("60.00"))
                                .date(LocalDate.of(2026, 4, 5))
                                .category("Food")
                                .type(TransactionType.EXPENSE)
                                .build(),
                        Transaction.builder()
                                .amount(new BigDecimal("50.00"))
                                .date(LocalDate.of(2026, 4, 6))
                                .category("food")
                                .type(TransactionType.EXPENSE)
                                .build()
                ));

        Transaction trigger = Transaction.builder()
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2026, 4, 6))
                .category("food")
                .type(TransactionType.EXPENSE)
                .build();

        Optional<String> warning = budgetService.buildOverBudgetWarningIfAny(trigger);

        assertThat(warning).isPresent();
        assertThat(warning.get()).contains("Over budget");
        assertThat(warning.get()).contains("Food");
    }

    @Test
    void buildOverBudgetWarningIfAny_income_returnsEmpty() {
        Transaction income = Transaction.builder()
                .amount(new BigDecimal("500.00"))
                .date(LocalDate.of(2026, 4, 1))
                .category("Salary")
                .type(TransactionType.INCOME)
                .build();

        assertThat(budgetService.buildOverBudgetWarningIfAny(income)).isEmpty();
    }
}
