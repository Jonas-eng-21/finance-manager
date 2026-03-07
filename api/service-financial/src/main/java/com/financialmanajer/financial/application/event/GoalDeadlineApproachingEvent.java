package com.financialmanajer.financial.application.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalDeadlineApproachingEvent(
        Long goalId,
        Long userId,
        String goalName,
        LocalDate deadline,
        BigDecimal remainingAmount,
        long daysRemaining
) {}