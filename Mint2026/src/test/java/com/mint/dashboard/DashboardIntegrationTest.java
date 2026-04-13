package com.mint.dashboard;

import com.mint.dashboard.dto.AnalyticsResponseDto;
import com.mint.dashboard.dto.DashboardSummaryDto;
import com.mint.entity.Bank;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.entity.User;
import com.mint.repository.BankConnectionRepository;
import com.mint.transaction.Transaction;
import com.mint.transaction.TransactionRepository;
import com.mint.transaction.TransactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class DashboardIntegrationTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BankConnectionRepository bankConnectionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void getSummary_combinesTransactionsAndLinkedBankSyncData() {
        transactionRepository.save(buildTransaction("Salary", TransactionType.INCOME, "3000.00", LocalDate.of(2025, 1, 3)));
        transactionRepository.save(buildTransaction("Freelance", TransactionType.INCOME, "500.00", LocalDate.of(2025, 1, 12)));
        transactionRepository.save(buildTransaction("Food", TransactionType.EXPENSE, "200.00", LocalDate.of(2025, 1, 20)));

        User user = persistUser();
        Bank bank = persistBank();

        bankConnectionRepository.save(buildConnection(user, bank, ConnectionStatus.LINKED, LocalDateTime.of(2025, 1, 19, 9, 0)));
        bankConnectionRepository.save(buildConnection(user, bank, ConnectionStatus.LINKED, LocalDateTime.of(2025, 1, 27, 11, 30)));
        bankConnectionRepository.save(buildConnection(user, bank, ConnectionStatus.PENDING, null));

        DashboardSummaryDto summary = dashboardService.getSummary();

        assertThat(summary.getTotalIncome()).isEqualByComparingTo("3500.00");
        assertThat(summary.getTotalExpense()).isEqualByComparingTo("200.00");
        assertThat(summary.getTotalBalance()).isEqualByComparingTo("3300.00");
        assertThat(summary.getTransactionCount()).isEqualTo(3);
        assertThat(summary.getLinkedBankAccounts()).isEqualTo(2);
        assertThat(summary.getLastBankSyncDate()).isEqualTo(LocalDateTime.of(2025, 1, 27, 11, 30));
    }

    @Test
    void getAnalytics_returnsExpenseAggregationWithinDateRange() {
        transactionRepository.save(buildTransaction("Food", TransactionType.EXPENSE, "120.00", LocalDate.of(2025, 2, 5)));
        transactionRepository.save(buildTransaction("Food", TransactionType.EXPENSE, "80.00", LocalDate.of(2025, 2, 6)));
        transactionRepository.save(buildTransaction("Rent", TransactionType.EXPENSE, "900.00", LocalDate.of(2025, 2, 7)));
        transactionRepository.save(buildTransaction("Salary", TransactionType.INCOME, "2000.00", LocalDate.of(2025, 2, 8)));
        transactionRepository.save(buildTransaction("Travel", TransactionType.EXPENSE, "50.00", LocalDate.of(2025, 1, 25)));

        AnalyticsResponseDto analytics = dashboardService.getAnalytics(
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28)
        );

        assertThat(analytics.getTotalSpending()).isEqualByComparingTo("1100.00");
        assertThat(analytics.getCategories()).hasSize(2);
        assertThat(analytics.getCategories())
                .anySatisfy(category -> {
                    assertThat(category.getCategory()).isEqualTo("Food");
                    assertThat(category.getTotalAmount()).isEqualByComparingTo("200.00");
                    assertThat(category.getTransactionCount()).isEqualTo(2);
                })
                .anySatisfy(category -> {
                    assertThat(category.getCategory()).isEqualTo("Rent");
                    assertThat(category.getTotalAmount()).isEqualByComparingTo("900.00");
                    assertThat(category.getTransactionCount()).isEqualTo(1);
                });
    }

    private Transaction buildTransaction(String category, TransactionType type, String amount, LocalDate date) {
        return Transaction.builder()
                .category(category)
                .type(type)
                .amount(new BigDecimal(amount))
                .date(date)
                .build();
    }

    private User persistUser() {
        User user = User.builder()
                .username("dashboard_user_" + System.nanoTime())
                .email("dashboard_" + System.nanoTime() + "@test.com")
                .password("password123")
                .build();
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Bank persistBank() {
        Bank bank = Bank.builder()
                .name("Dashboard Test Bank")
                .apiEndpoint("https://api.dashboard.test")
                .build();
        entityManager.persist(bank);
        entityManager.flush();
        return bank;
    }

    private BankConnection buildConnection(User user, Bank bank, ConnectionStatus status, LocalDateTime lastSyncDate) {
        return BankConnection.builder()
                .user(user)
                .bank(bank)
                .accountId("ACC_" + System.nanoTime())
                .status(status)
                .syncStatus(status == ConnectionStatus.LINKED ? SyncStatus.SUCCESS : SyncStatus.IDLE)
                .lastSyncDate(lastSyncDate)
                .syncFailureCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

