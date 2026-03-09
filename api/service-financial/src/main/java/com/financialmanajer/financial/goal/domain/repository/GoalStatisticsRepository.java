package com.financialmanajer.financial.goal.domain.repository;

import com.financialmanajer.financial.goal.domain.model.GoalStatistics;

public interface GoalStatisticsRepository {
    GoalStatistics getStatisticsByUserId(Long userId);
}