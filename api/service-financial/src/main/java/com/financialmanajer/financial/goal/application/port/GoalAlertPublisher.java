package com.financialmanajer.financial.goal.application.port;

import com.financialmanajer.financial.goal.application.event.GoalDeadlineApproachingEvent;

public interface GoalAlertPublisher {
    void publish(GoalDeadlineApproachingEvent event);
}