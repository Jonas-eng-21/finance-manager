package com.financialmanajer.financial.goal.presentation.dto;

import com.financialmanajer.financial.goal.domain.model.GoalStatistics;
import java.math.BigDecimal;

public record GoalStatisticsResponse(
        Long totalGoals,
        Long completedGoals,
        Long activeGoals,
        Double completionRate,
        BigDecimal totalSavedAmount,
        Integer averageCompletionDays
) {
    public static GoalStatisticsResponse fromDomain(GoalStatistics stats) {
        return new GoalStatisticsResponse(
                stats.totalGoals(),
                stats.completedGoals(),
                stats.activeGoals(),
                stats.getCompletionRate(),
                stats.totalSavedAmount(),
                stats.averageCompletionDays().intValue()
        );
    }
}