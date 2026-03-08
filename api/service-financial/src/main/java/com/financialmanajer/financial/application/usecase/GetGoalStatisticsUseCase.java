package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.domain.model.GoalStatistics;
import com.financialmanajer.financial.domain.repository.GoalStatisticsRepository;

public class GetGoalStatisticsUseCase {

    private final GoalStatisticsRepository statisticsRepository;

    public GetGoalStatisticsUseCase(GoalStatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public GoalStatistics execute(Long userId) {
        return statisticsRepository.getStatisticsByUserId(userId);
    }
}