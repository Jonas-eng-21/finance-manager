package com.financialmanajer.financial.goal.application.usecase;

import com.financialmanajer.financial.goal.domain.model.GoalStatistics;
import com.financialmanajer.financial.goal.domain.repository.GoalStatisticsRepository;

public class GetGoalStatisticsUseCase {

    private final GoalStatisticsRepository statisticsRepository;

    public GetGoalStatisticsUseCase(GoalStatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public GoalStatistics execute(Long userId) {
        return statisticsRepository.getStatisticsByUserId(userId);
    }
}