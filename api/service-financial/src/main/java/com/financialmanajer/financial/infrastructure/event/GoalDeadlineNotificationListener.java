package com.financialmanajer.financial.infrastructure.event;

import com.financialmanajer.financial.application.event.GoalDeadlineApproachingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class GoalDeadlineNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(GoalDeadlineNotificationListener.class);
    private final MessageSource messageSource;

    public GoalDeadlineNotificationListener(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Async
    @EventListener
    public void handleGoalDeadlineEvent(GoalDeadlineApproachingEvent event) {
        String message = messageSource.getMessage(
                "goal.alert.deadline_approaching",
                new Object[]{event.userId(), event.goalName(), event.daysRemaining(), event.remainingAmount()},
                Locale.getDefault()
        );

        log.info(message);
    }
}