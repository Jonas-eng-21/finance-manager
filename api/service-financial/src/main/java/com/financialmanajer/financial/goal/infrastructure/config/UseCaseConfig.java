package com.financialmanajer.financial.goal.infrastructure.config;

import com.financialmanajer.financial.goal.application.port.GoalAlertPublisher;
import com.financialmanajer.financial.goal.application.usecase.CheckGoalsDeadlineUseCase;
import com.financialmanajer.financial.goal.application.usecase.DeleteGoalUseCase;
import com.financialmanajer.financial.goal.application.usecase.GetGoalStatisticsUseCase;
import com.financialmanajer.financial.goal.domain.repository.GoalRepository;
import com.financialmanajer.financial.goal.domain.repository.GoalStatisticsRepository;
import com.financialmanajer.financial.goal.application.usecase.ListArchivedGoalsUseCase;
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
    public ListArchivedGoalsUseCase listArchivedGoalsUseCase(
            GoalRepository goalRepository) {
        return new ListArchivedGoalsUseCase(goalRepository);
    }

    @org.springframework.context.annotation.Bean
    public GetGoalStatisticsUseCase getGoalStatisticsUseCase(GoalStatisticsRepository goalStatisticsRepository) {
        return new GetGoalStatisticsUseCase(goalStatisticsRepository);
    }
}