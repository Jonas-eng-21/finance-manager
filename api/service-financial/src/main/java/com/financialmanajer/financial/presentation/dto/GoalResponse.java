package com.financialmanajer.financial.presentation.dto;

import com.financialmanajer.financial.domain.model.Goal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(
        Long id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate startDate,
        LocalDate targetDate,
        BigDecimal monthlyRequiredSaving,
        LocalDateTime createdAt
) {
    public static GoalResponse fromDomain(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getStartDate(),
                goal.getTargetDate(),
                goal.calculateMonthlyRequiredSaving(LocalDate.now()),
                goal.getCreatedAt()
        );
    }
}