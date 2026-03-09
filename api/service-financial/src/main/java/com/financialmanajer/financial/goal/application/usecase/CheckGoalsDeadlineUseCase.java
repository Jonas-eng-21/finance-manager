package com.financialmanajer.financial.goal.application.usecase;

import com.financialmanajer.financial.goal.application.event.GoalDeadlineApproachingEvent;
import com.financialmanajer.financial.goal.application.port.GoalAlertPublisher;
import com.financialmanajer.financial.goal.domain.model.Goal;
import com.financialmanajer.financial.goal.domain.repository.GoalRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CheckGoalsDeadlineUseCase {

    private static final int ALERT_THRESHOLD_DAYS = 7;

    private final GoalRepository goalRepository;
    private final GoalAlertPublisher goalAlertPublisher;

    public CheckGoalsDeadlineUseCase(GoalRepository goalRepository, GoalAlertPublisher goalAlertPublisher) {
        this.goalRepository = goalRepository;
        this.goalAlertPublisher = goalAlertPublisher;
    }

    public void execute(LocalDate today) {
        List<Goal> activeGoals = goalRepository.findAllActive();

        activeGoals.stream()
                .filter(goal -> !goal.isCompleted())
                .filter(goal -> goal.isNearDeadline(today, ALERT_THRESHOLD_DAYS))
                .forEach(goal -> {
                    long daysRemaining = ChronoUnit.DAYS.between(today, goal.getTargetDate());

                    GoalDeadlineApproachingEvent event = new GoalDeadlineApproachingEvent(
                            goal.getId(),
                            goal.getUserId(),
                            goal.getName(),
                            goal.getTargetDate(),
                            goal.getTargetAmount().subtract(goal.getCurrentAmount()),
                            daysRemaining
                    );

                    goalAlertPublisher.publish(event);
                });
    }
}