package com.mint.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequestDto {

    @NotBlank
    @Size(max = 128)
    private String category;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal monthlyLimit;
}
