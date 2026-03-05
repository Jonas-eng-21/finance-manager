package com.financialmanajer.financial.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalDTO(
        Long userId,
        String name,
        BigDecimal targetAmount,
        LocalDate startDate,
        LocalDate targetDate
) {}