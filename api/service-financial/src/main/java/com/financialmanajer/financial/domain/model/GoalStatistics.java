package com.financialmanajer.financial.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record GoalStatistics(
        Long totalGoals,
        Long completedGoals,
        Long activeGoals,
        BigDecimal totalSavedAmount,
        Double averageCompletionDays
) {

    public GoalStatistics(Long totalGoals, Long completedGoals, Long activeGoals, BigDecimal totalSavedAmount) {
        this(totalGoals, completedGoals, activeGoals, totalSavedAmount, 0.0);
    }

    public GoalStatistics {
        totalGoals = totalGoals == null ? 0L : totalGoals;
        completedGoals = completedGoals == null ? 0L : completedGoals;
        activeGoals = activeGoals == null ? 0L : activeGoals;
        totalSavedAmount = totalSavedAmount == null ? BigDecimal.ZERO : totalSavedAmount;
        averageCompletionDays = averageCompletionDays == null ? 0.0 : averageCompletionDays;
    }

    public Double getCompletionRate() {
        if (totalGoals == 0) return 0.0;
        return BigDecimal.valueOf(completedGoals)
                .divide(BigDecimal.valueOf(totalGoals), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}