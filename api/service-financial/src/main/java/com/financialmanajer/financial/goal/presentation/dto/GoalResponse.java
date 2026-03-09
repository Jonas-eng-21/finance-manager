package com.financialmanajer.financial.goal.presentation.dto;

import com.financialmanajer.financial.goal.domain.model.Goal;
import com.financialmanajer.financial.goal.domain.model.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(
        Long id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        BigDecimal progressPercentage,
        GoalStatus status,
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
                goal.calculateRemainingAmount(),
                goal.calculateProgressPercentage(),
                goal.getStatus(LocalDate.now()),
                goal.getStartDate(),
                goal.getTargetDate(),
                goal.calculateMonthlyRequiredSaving(LocalDate.now()),
                goal.getCreatedAt()
        );
    }
}