package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.domain.model.GoalStatistics;
import com.financialmanajer.financial.domain.repository.GoalStatisticsRepository;
import com.financialmanajer.financial.infrastructure.persistence.entity.GoalEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class GoalStatisticsRepositoryImpl implements GoalStatisticsRepository {

    private final SpringDataGoalStatisticsRepository springDataRepository;

    public GoalStatisticsRepositoryImpl(SpringDataGoalStatisticsRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public GoalStatistics getStatisticsByUserId(Long userId) {
        GoalStatistics stats = springDataRepository.getStatisticsByUserId(userId);

        if (stats == null || stats.totalGoals() == 0L) {
            return new GoalStatistics(0L, 0L, 0L, BigDecimal.ZERO, 0.0);
        }

        List<GoalEntity> archivedGoals = springDataRepository.findArchivedGoalsByUserId(userId);

        double averageDays = 0.0;
        if (archivedGoals != null && !archivedGoals.isEmpty()) {
            long totalDays = 0;
            for (GoalEntity goal : archivedGoals) {
                if (goal.getCreatedAt() != null && goal.getArchivedAt() != null) {
                    totalDays += ChronoUnit.DAYS.between(goal.getCreatedAt(), goal.getArchivedAt());
                }
            }
            averageDays = (double) totalDays / archivedGoals.size();
        }

        return new GoalStatistics(
                stats.totalGoals(),
                stats.completedGoals(),
                stats.activeGoals(),
                stats.totalSavedAmount(),
                averageDays
        );
    }
}