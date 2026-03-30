package com.mint.dashboard;

import com.mint.dashboard.dto.AnalyticsResponseDto;
import com.mint.dashboard.dto.CategorySpendingDto;
import com.mint.dashboard.dto.DashboardSummaryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    // -------------------------------------------------------------------------
    // GET /api/dashboard/summary
    // -------------------------------------------------------------------------

    @Test
    void getSummary_returnsOkWithBody() {
        DashboardSummaryDto dto = DashboardSummaryDto.builder()
                .totalIncome(new BigDecimal("3000.00"))
                .totalExpense(new BigDecimal("500.00"))
                .totalBalance(new BigDecimal("2500.00"))
                .transactionCount(5)
                .linkedBankAccounts(2)
                .lastBankSyncDate(LocalDateTime.of(2025, 1, 20, 10, 0))
                .build();

        when(dashboardService.getSummary()).thenReturn(dto);

        var response = dashboardController.getSummary();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalBalance()).isEqualByComparingTo("2500.00");
        assertThat(response.getBody().getTotalIncome()).isEqualByComparingTo("3000.00");
        assertThat(response.getBody().getTotalExpense()).isEqualByComparingTo("500.00");
        assertThat(response.getBody().getTransactionCount()).isEqualTo(5);
        assertThat(response.getBody().getLinkedBankAccounts()).isEqualTo(2);
        assertThat(response.getBody().getLastBankSyncDate()).isNotNull();
    }

    @Test
    void getSummary_noLinkedBanks_returnsZeroLinkedAccounts() {
        DashboardSummaryDto dto = DashboardSummaryDto.builder()
                .totalIncome(BigDecimal.ZERO)
                .totalExpense(BigDecimal.ZERO)
                .totalBalance(BigDecimal.ZERO)
                .transactionCount(0)
                .linkedBankAccounts(0)
                .lastBankSyncDate(null)
                .build();

        when(dashboardService.getSummary()).thenReturn(dto);

        var response = dashboardController.getSummary();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getLinkedBankAccounts()).isZero();
        assertThat(response.getBody().getLastBankSyncDate()).isNull();
    }

    // -------------------------------------------------------------------------
    // GET /api/dashboard/analytics?from=&to=
    // -------------------------------------------------------------------------

    @Test
    void getAnalytics_validDateRange_returnsOkWithBody() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to   = LocalDate.of(2025, 1, 31);

        AnalyticsResponseDto dto = AnalyticsResponseDto.builder()
                .from(from)
                .to(to)
                .totalSpending(new BigDecimal("350.00"))
                .categories(List.of(
                        CategorySpendingDto.builder()
                                .category("Food")
                                .totalAmount(new BigDecimal("200.00"))
                                .transactionCount(2)
                                .build(),
                        CategorySpendingDto.builder()
                                .category("Transport")
                                .totalAmount(new BigDecimal("150.00"))
                                .transactionCount(3)
                                .build()
                ))
                .build();

        when(dashboardService.getAnalytics(from, to)).thenReturn(dto);

        var response = dashboardController.getAnalytics(from, to);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalSpending()).isEqualByComparingTo("350.00");
        assertThat(response.getBody().getCategories()).hasSize(2);
        assertThat(response.getBody().getFrom()).isEqualTo(from);
        assertThat(response.getBody().getTo()).isEqualTo(to);
    }

    @Test
    void getAnalytics_fromAfterTo_returnsBadRequest() {
        LocalDate from = LocalDate.of(2025, 2, 1);
        LocalDate to   = LocalDate.of(2025, 1, 1); // to is before from

        var response = dashboardController.getAnalytics(from, to);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAnalytics_sameDayRange_returnsOk() {
        LocalDate sameDay = LocalDate.of(2025, 6, 15);

        AnalyticsResponseDto dto = AnalyticsResponseDto.builder()
                .from(sameDay).to(sameDay)
                .totalSpending(new BigDecimal("50.00"))
                .categories(List.of(
                        CategorySpendingDto.builder()
                                .category("Coffee")
                                .totalAmount(new BigDecimal("50.00"))
                                .transactionCount(1)
                                .build()
                ))
                .build();

        when(dashboardService.getAnalytics(sameDay, sameDay)).thenReturn(dto);

        var response = dashboardController.getAnalytics(sameDay, sameDay);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCategories()).hasSize(1);
    }

    @Test
    void getAnalytics_noSpendingInRange_returnsEmptyCategories() {
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to   = LocalDate.of(2025, 3, 31);

        AnalyticsResponseDto dto = AnalyticsResponseDto.builder()
                .from(from).to(to)
                .totalSpending(BigDecimal.ZERO)
                .categories(List.of())
                .build();

        when(dashboardService.getAnalytics(from, to)).thenReturn(dto);

        var response = dashboardController.getAnalytics(from, to);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTotalSpending()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getBody().getCategories()).isEmpty();
    }
}
