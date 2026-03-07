package com.financialmanajer.financial.application.port;

import com.financialmanajer.financial.application.event.GoalDeadlineApproachingEvent;

public interface GoalAlertPublisher {
    void publish(GoalDeadlineApproachingEvent event);
}