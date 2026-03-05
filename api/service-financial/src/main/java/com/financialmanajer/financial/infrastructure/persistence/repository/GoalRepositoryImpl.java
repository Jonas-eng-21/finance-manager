package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import com.financialmanajer.financial.infrastructure.persistence.entity.GoalEntity;
import org.springframework.stereotype.Repository;

@Repository
public class GoalRepositoryImpl implements GoalRepository {

    private final SpringDataGoalRepository springDataRepository;

    public GoalRepositoryImpl(SpringDataGoalRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public boolean existsByNameAndUserId(String name, Long userId) {
        return springDataRepository.existsByNameIgnoreCaseAndUserIdAndDeletedAtIsNull(name, userId);
    }

    @Override
    public Goal save(Goal goal) {
        GoalEntity entity = toEntity(goal);
        GoalEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    private GoalEntity toEntity(Goal goal) {
        GoalEntity entity = new GoalEntity();
        entity.setId(goal.getId());
        entity.setUserId(goal.getUserId());
        entity.setName(goal.getName());
        entity.setTargetAmount(goal.getTargetAmount());
        entity.setCurrentAmount(goal.getCurrentAmount());
        entity.setStartDate(goal.getStartDate());
        entity.setTargetDate(goal.getTargetDate());
        entity.setCreatedAt(goal.getCreatedAt());
        entity.setUpdatedAt(goal.getUpdatedAt());
        entity.setDeletedAt(goal.getDeletedAt());
        return entity;
    }

    private Goal toDomain(GoalEntity entity) {
        Goal goal = new Goal(
                entity.getUserId(),
                entity.getName(),
                entity.getTargetAmount(),
                entity.getStartDate(),
                entity.getTargetDate()
        );
        goal.setId(entity.getId());
        goal.loadCurrentAmount(entity.getCurrentAmount());
        try {
            java.lang.reflect.Field createdAtField = Goal.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(goal, entity.getCreatedAt());
        } catch (Exception ignored) {}

        goal.loadUpdatedAt(entity.getUpdatedAt());
        goal.loadDeletedAt(entity.getDeletedAt());
        return goal;
    }
}