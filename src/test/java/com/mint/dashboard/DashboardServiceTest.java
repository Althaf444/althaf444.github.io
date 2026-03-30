package com.mint.dashboard;

import com.mint.dashboard.dto.AnalyticsResponseDto;
import com.mint.dashboard.dto.DashboardSummaryDto;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.repository.BankConnectionRepository;
import com.mint.transaction.Transaction;
import com.mint.transaction.TransactionRepository;
import com.mint.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankConnectionRepository bankConnectionRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Transaction income1;
    private Transaction income2;
    private Transaction expense1;
    private Transaction expense2;

    @BeforeEach
    void setUp() {
        income1 = Transaction.builder()
                .id(1L).amount(new BigDecimal("3000.00"))
                .date(LocalDate.of(2025, 1, 5))
                .category("Salary").type(TransactionType.INCOME).build();

        income2 = Transaction.builder()
                .id(2L).amount(new BigDecimal("500.00"))
                .date(LocalDate.of(2025, 1, 10))
                .category("Freelance").type(TransactionType.INCOME).build();

        expense1 = Transaction.builder()
                .id(3L).amount(new BigDecimal("200.00"))
                .date(LocalDate.of(2025, 1, 12))
                .category("Food").type(TransactionType.EXPENSE).build();

        expense2 = Transaction.builder()
                .id(4L).amount(new BigDecimal("150.00"))
                .date(LocalDate.of(2025, 1, 20))
                .category("Food").type(TransactionType.EXPENSE).build();
    }

    // =========================================================================
    // getSummary() — Transaction data
    // =========================================================================

    @Test
    void getSummary_returnsCorrectTransactionTotals() {
        when(transactionRepository.findAll()).thenReturn(List.of(income1, income2, expense1, expense2));
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED)).thenReturn(List.of());

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getTotalIncome()).isEqualByComparingTo("3500.00");
        assertThat(summary.getTotalExpense()).isEqualByComparingTo("350.00");
        assertThat(summary.getTotalBalance()).isEqualByComparingTo("3150.00");
        assertThat(summary.getTransactionCount()).isEqualTo(4);
    }

    @Test
    void getSummary_emptyTransactions_returnsZeros() {
        when(transactionRepository.findAll()).thenReturn(List.of());
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED)).thenReturn(List.of());

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getTotalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTransactionCount()).isZero();
    }

    @Test
    void getSummary_onlyExpenses_balanceIsNegative() {
        when(transactionRepository.findAll()).thenReturn(List.of(expense1, expense2));
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED)).thenReturn(List.of());

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getTotalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalBalance()).isEqualByComparingTo("-350.00");
    }

    // =========================================================================
    // getSummary() — Bank Connection integration
    // =========================================================================

    @Test
    void getSummary_withLinkedBanks_returnsCorrectCount() {
        BankConnection bank1 = buildLinkedConnection(LocalDateTime.of(2025, 1, 10, 8, 0));
        BankConnection bank2 = buildLinkedConnection(LocalDateTime.of(2025, 1, 20, 12, 0));

        when(transactionRepository.findAll()).thenReturn(List.of());
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED))
                .thenReturn(List.of(bank1, bank2));

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getLinkedBankAccounts()).isEqualTo(2);
    }

    @Test
    void getSummary_withLinkedBanks_returnsLatestSyncDate() {
        LocalDateTime older = LocalDateTime.of(2025, 1, 10, 8, 0);
        LocalDateTime latest = LocalDateTime.of(2025, 1, 20, 12, 0);

        BankConnection bank1 = buildLinkedConnection(older);
        BankConnection bank2 = buildLinkedConnection(latest);

        when(transactionRepository.findAll()).thenReturn(List.of());
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED))
                .thenReturn(List.of(bank1, bank2));

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getLastBankSyncDate()).isEqualTo(latest);
    }

    @Test
    void getSummary_noLinkedBanks_returnsZeroAndNullSyncDate() {
        when(transactionRepository.findAll()).thenReturn(List.of());
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED)).thenReturn(List.of());

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getLinkedBankAccounts()).isZero();
        assertThat(summary.getLastBankSyncDate()).isNull();
    }

    @Test
    void getSummary_linkedBanksNeverSynced_lastSyncDateIsNull() {
        BankConnection neverSynced = buildLinkedConnection(null);

        when(transactionRepository.findAll()).thenReturn(List.of());
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED))
                .thenReturn(List.of(neverSynced));

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getLinkedBankAccounts()).isEqualTo(1);
        assertThat(summary.getLastBankSyncDate()).isNull();
    }

    @Test
    void getSummary_combinesTransactionAndBankData() {
        LocalDateTime syncDate = LocalDateTime.of(2025, 1, 25, 9, 30);

        when(transactionRepository.findAll()).thenReturn(List.of(income1, expense1));
        when(bankConnectionRepository.findByStatus(ConnectionStatus.LINKED))
                .thenReturn(List.of(buildLinkedConnection(syncDate)));

        DashboardSummaryDto summary = dashboardService.getSummary();

        // Transaction data
        assertThat(summary.getTotalBalance()).isEqualByComparingTo("2800.00");
        assertThat(summary.getTransactionCount()).isEqualTo(2);
        // Bank data
        assertThat(summary.getLinkedBankAccounts()).isEqualTo(1);
        assertThat(summary.getLastBankSyncDate()).isEqualTo(syncDate);
    }

    // =========================================================================
    // getAnalytics() — Workflow C
    // =========================================================================

    @Test
    void getAnalytics_groupsExpensesByCategory() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 31);

        when(transactionRepository.findByTypeAndDateBetween(TransactionType.EXPENSE, from, to))
                .thenReturn(List.of(expense1, expense2));

        AnalyticsResponseDto analytics = dashboardService.getAnalytics(from, to);

        assertThat(analytics.getFrom()).isEqualTo(from);
        assertThat(analytics.getTo()).isEqualTo(to);
        assertThat(analytics.getTotalSpending()).isEqualByComparingTo("350.00");
        assertThat(analytics.getCategories()).hasSize(1);
        assertThat(analytics.getCategories().get(0).getCategory()).isEqualTo("Food");
        assertThat(analytics.getCategories().get(0).getTotalAmount()).isEqualByComparingTo("350.00");
        assertThat(analytics.getCategories().get(0).getTransactionCount()).isEqualTo(2);
    }

    @Test
    void getAnalytics_noExpensesInRange_returnsEmptyCategories() {
        LocalDate from = LocalDate.of(2025, 2, 1);
        LocalDate to = LocalDate.of(2025, 2, 28);

        when(transactionRepository.findByTypeAndDateBetween(TransactionType.EXPENSE, from, to))
                .thenReturn(List.of());

        AnalyticsResponseDto analytics = dashboardService.getAnalytics(from, to);

        assertThat(analytics.getTotalSpending()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(analytics.getCategories()).isEmpty();
    }

    @Test
    void getAnalytics_multipleCategories_eachAggregatedSeparately() {
        Transaction rentExpense = Transaction.builder()
                .id(5L).amount(new BigDecimal("1200.00"))
                .date(LocalDate.of(2025, 1, 15))
                .category("Rent").type(TransactionType.EXPENSE).build();

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 31);

        when(transactionRepository.findByTypeAndDateBetween(TransactionType.EXPENSE, from, to))
                .thenReturn(List.of(expense1, expense2, rentExpense));

        AnalyticsResponseDto analytics = dashboardService.getAnalytics(from, to);

        assertThat(analytics.getCategories()).hasSize(2);
        assertThat(analytics.getTotalSpending()).isEqualByComparingTo("1550.00");
    }

    @Test
    void getAnalytics_singleDayRange_returnsTransactionsForThatDay() {
        LocalDate sameDay = LocalDate.of(2025, 1, 12);

        when(transactionRepository.findByTypeAndDateBetween(TransactionType.EXPENSE, sameDay, sameDay))
                .thenReturn(List.of(expense1));

        AnalyticsResponseDto analytics = dashboardService.getAnalytics(sameDay, sameDay);

        assertThat(analytics.getCategories()).hasSize(1);
        assertThat(analytics.getTotalSpending()).isEqualByComparingTo("200.00");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private BankConnection buildLinkedConnection(LocalDateTime lastSyncDate) {
        return BankConnection.builder()
                .id(1L)
                .accountId("ACC_TEST")
                .status(ConnectionStatus.LINKED)
                .syncStatus(SyncStatus.SUCCESS)
                .lastSyncDate(lastSyncDate)
                .syncFailureCount(0)
                .build();
    }
}
