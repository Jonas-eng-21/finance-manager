package com.financialmanajer.financial.goal.infrastructure.event;

import com.financialmanajer.financial.goal.application.event.GoalDeadlineApproachingEvent;
import com.financialmanajer.financial.goal.application.port.GoalAlertPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringGoalAlertPublisher implements GoalAlertPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringGoalAlertPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(GoalDeadlineApproachingEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}