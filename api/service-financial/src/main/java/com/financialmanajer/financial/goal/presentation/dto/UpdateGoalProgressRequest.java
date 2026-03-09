package com.financialmanajer.financial.goal.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateGoalProgressRequest(
        @NotNull(message = "{goal.validation.progress.positive}")
        @Positive(message = "{goal.validation.progress.positive}")
        BigDecimal amount
) {}