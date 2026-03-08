package com.financialmanajer.financial.infrastructure.config;

import com.financialmanajer.financial.application.port.GoalAlertPublisher;
import com.financialmanajer.financial.application.usecase.CheckGoalsDeadlineUseCase;
import com.financialmanajer.financial.application.usecase.DeleteGoalUseCase;
import com.financialmanajer.financial.application.usecase.GetGoalStatisticsUseCase;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import com.financialmanajer.financial.domain.repository.GoalStatisticsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CheckGoalsDeadlineUseCase checkGoalsDeadlineUseCase(
            GoalRepository goalRepository,
            GoalAlertPublisher goalAlertPublisher) {

        return new CheckGoalsDeadlineUseCase(goalRepository, goalAlertPublisher);
    }

    @org.springframework.context.annotation.Bean
    public DeleteGoalUseCase deleteGoalUseCase(GoalRepository goalRepository) {
        return new DeleteGoalUseCase(goalRepository);
    }

    @org.springframework.context.annotation.Bean
    public com.financialmanajer.financial.application.usecase.ListArchivedGoalsUseCase listArchivedGoalsUseCase(
            com.financialmanajer.financial.domain.repository.GoalRepository goalRepository) {
        return new com.financialmanajer.financial.application.usecase.ListArchivedGoalsUseCase(goalRepository);
    }

    @org.springframework.context.annotation.Bean
    public GetGoalStatisticsUseCase getGoalStatisticsUseCase(GoalStatisticsRepository goalStatisticsRepository) {
        return new GetGoalStatisticsUseCase(goalStatisticsRepository);
    }
}