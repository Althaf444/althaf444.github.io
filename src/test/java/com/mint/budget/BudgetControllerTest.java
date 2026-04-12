package com.mint.budget;

import com.mint.budget.dto.BudgetCategoryStatusDto;
import com.mint.budget.dto.BudgetOverviewDto;
import com.mint.budget.dto.BudgetRequestDto;
import com.mint.budget.dto.BudgetResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    @Test
    void overview_validMonth_returnsOk() {
        BudgetOverviewDto dto = BudgetOverviewDto.builder()
                .year(2026)
                .month(4)
                .totalBudgeted(new BigDecimal("500.00"))
                .totalSpent(new BigDecimal("200.00"))
                .totalRemaining(new BigDecimal("300.00"))
                .categories(List.of(
                        BudgetCategoryStatusDto.builder()
                                .budgetId(1L)
                                .category("Food")
                                .monthlyLimit(new BigDecimal("500.00"))
                                .spentAmount(new BigDecimal("200.00"))
                                .remainingAmount(new BigDecimal("300.00"))
                                .percentUsed(new BigDecimal("40.00"))
                                .overBudget(false)
                                .periodStart(LocalDate.of(2026, 4, 1))
                                .periodEnd(LocalDate.of(2026, 4, 30))
                                .build()
                ))
                .build();

        when(budgetService.getMonthlyOverview(2026, 4)).thenReturn(dto);

        var response = budgetController.overview(2026, 4);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalSpent()).isEqualByComparingTo("200.00");
        assertThat(response.getBody().getCategories()).hasSize(1);
    }

    @Test
    void get_notFound_returns404() {
        when(budgetService.getBudget(99L)).thenThrow(new IllegalArgumentException("Budget not found with id: 99"));

        var response = budgetController.get(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void create_duplicateCategory_returnsBadRequest() {
        BudgetRequestDto req = BudgetRequestDto.builder()
                .category("Food")
                .monthlyLimit(new BigDecimal("400.00"))
                .build();
        when(budgetService.createBudget(any())).thenThrow(
                new IllegalArgumentException("A budget already exists for category: Food"));

        var response = budgetController.create(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void delete_success_returnsNoContent() {
        var response = budgetController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(budgetService).deleteBudget(1L);
    }

    @Test
    void update_notFound_returns404() {
        BudgetRequestDto req = BudgetRequestDto.builder()
                .category("Food")
                .monthlyLimit(new BigDecimal("100.00"))
                .build();
        when(budgetService.updateBudget(eq(5L), any())).thenThrow(
                new IllegalArgumentException("Budget not found with id: 5"));

        var response = budgetController.update(5L, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
