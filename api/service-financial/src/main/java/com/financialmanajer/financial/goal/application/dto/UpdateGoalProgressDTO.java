package com.financialmanajer.financial.goal.application.dto;

import java.math.BigDecimal;

public record UpdateGoalProgressDTO(
        Long goalId,
        Long userId,
        BigDecimal amount
) {}