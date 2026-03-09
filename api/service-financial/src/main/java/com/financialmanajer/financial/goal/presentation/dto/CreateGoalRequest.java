package com.financialmanajer.financial.goal.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalRequest(
        @NotBlank(message = "{goal.validation.name.required}")
        @Size(min = 3, max = 100, message = "{goal.validation.name.size}")
        String name,

        @NotNull(message = "{goal.validation.target_amount.positive}")
        @Positive(message = "{goal.validation.target_amount.positive}")
        BigDecimal targetAmount,

        @NotNull(message = "{goal.validation.date.required}")
        LocalDate startDate,

        @NotNull(message = "{goal.validation.date.required}")
        LocalDate targetDate
) {}