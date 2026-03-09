package com.financialmanajer.financial.goal.infrastructure.job;

import com.financialmanajer.financial.goal.application.usecase.CheckGoalsDeadlineUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Locale;

@Component
public class GoalAlertJob {

    private static final Logger log = LoggerFactory.getLogger(GoalAlertJob.class);
    private final CheckGoalsDeadlineUseCase checkGoalsDeadlineUseCase;
    private final MessageSource messageSource;

    public GoalAlertJob(CheckGoalsDeadlineUseCase checkGoalsDeadlineUseCase, MessageSource messageSource) {
        this.checkGoalsDeadlineUseCase = checkGoalsDeadlineUseCase;
        this.messageSource = messageSource;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void executeDailyAlertCheck() {
        String startMsg = messageSource.getMessage("job.goal_alert.start", null, Locale.getDefault());
        log.info(startMsg);

        checkGoalsDeadlineUseCase.execute(LocalDate.now());

        String finishMsg = messageSource.getMessage("job.goal_alert.finish", null, Locale.getDefault());
        log.info(finishMsg);
    }
}