package com.financialmanajer.financial.infrastructure.config;

import com.financialmanajer.financial.application.port.GoalAlertPublisher;
import com.financialmanajer.financial.application.usecase.CheckGoalsDeadlineUseCase;
import com.financialmanajer.financial.application.usecase.DeleteGoalUseCase;
import com.financialmanajer.financial.domain.repository.GoalRepository;
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
}