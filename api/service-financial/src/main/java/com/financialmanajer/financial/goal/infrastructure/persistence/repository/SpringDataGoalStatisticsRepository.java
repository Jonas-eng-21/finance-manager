package com.financialmanajer.financial.goal.infrastructure.persistence.repository;

import com.financialmanajer.financial.goal.domain.model.GoalStatistics;
import com.financialmanajer.financial.goal.infrastructure.persistence.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataGoalStatisticsRepository extends JpaRepository<GoalEntity, Long> {

    @Query("""
        SELECT new com.financialmanajer.financial.goal.domain.model.GoalStatistics(
            COUNT(g),
            SUM(CASE WHEN g.archivedAt IS NOT NULL THEN 1L ELSE 0L END),
            SUM(CASE WHEN g.archivedAt IS NULL THEN 1L ELSE 0L END),
            SUM(CASE WHEN g.archivedAt IS NOT NULL THEN g.currentAmount END)
        )
        FROM GoalEntity g
        WHERE g.userId = :userId
        AND g.deletedAt IS NULL
    """)
    GoalStatistics getStatisticsByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM GoalEntity g WHERE g.userId = :userId AND g.deletedAt IS NULL AND g.archivedAt IS NOT NULL")
    List<GoalEntity> findArchivedGoalsByUserId(@Param("userId") Long userId);
}