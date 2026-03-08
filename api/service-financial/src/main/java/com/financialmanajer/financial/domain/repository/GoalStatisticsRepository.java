package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.domain.model.GoalStatistics;

public interface GoalStatisticsRepository {
    GoalStatistics getStatisticsByUserId(Long userId);
}