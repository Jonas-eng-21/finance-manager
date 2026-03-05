package com.financialmanajer.financial.application.dto;

import java.math.BigDecimal;

public record UpdateGoalProgressDTO(
        Long goalId,
        Long userId,
        BigDecimal amount
) {}