package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import com.financialmanajer.financial.infrastructure.persistence.entity.GoalEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.financialmanajer.financial.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;
import java.util.List;
import java.util.Optional;

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
        entity.setArchivedAt(goal.getArchivedAt());
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
        goal.loadArchivedAt(entity.getArchivedAt());
        try {
            java.lang.reflect.Field createdAtField = Goal.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(goal, entity.getCreatedAt());
        } catch (Exception ignored) {}

        goal.loadUpdatedAt(entity.getUpdatedAt());
        goal.loadDeletedAt(entity.getDeletedAt());
        return goal;
    }

    @Override
    public PaginatedResult<Goal, Void> findAllActiveByUserId(Long userId, GoalFilterDTO filter) {
        String sortBy = filter.sortBy() != null && filter.sortBy().matches("^(targetDate|createdAt|name|targetAmount|currentAmount)$")
                ? filter.sortBy() : "targetDate";

        Sort.Direction direction = "desc".equalsIgnoreCase(filter.direction()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(filter.page(), filter.size(), Sort.by(direction, sortBy));

        Page<GoalEntity> page = springDataRepository.findByUserIdAndDeletedAtIsNullAndArchivedAtIsNull(userId, pageRequest);

        List<Goal> goals = page.getContent().stream()
                .map(this::toDomain)
                .toList();

        return new PaginatedResult<>(goals, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), null);
    }

    @Override
    public PaginatedResult<Goal, Void> findAllArchivedByUserId(Long userId, GoalFilterDTO filter) {
        String sortBy = filter.sortBy() != null && filter.sortBy().matches("^(targetDate|createdAt|name|targetAmount|currentAmount)$")
                ? filter.sortBy() : "targetDate";

        Sort.Direction direction = "desc".equalsIgnoreCase(filter.direction()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(filter.page(), filter.size(), Sort.by(direction, sortBy));

        Page<GoalEntity> page = springDataRepository.findByUserIdAndDeletedAtIsNullAndArchivedAtIsNotNull(userId, pageRequest);

        List<Goal> goals = page.getContent().stream()
                .map(this::toDomain)
                .toList();

        return new PaginatedResult<>(goals, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), null);
    }

    @Override
    public Optional<Goal> findByIdAndUserId(Long id, Long userId) {
        return springDataRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .map(this::toDomain);
    }

    @Override
    public List<Goal> findAllActive() {
        return springDataRepository.findByDeletedAtIsNullAndArchivedAtIsNull()
                .stream()
                .map(this::toDomain)
                .toList();
    }
}