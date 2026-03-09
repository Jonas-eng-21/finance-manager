package com.financialmanajer.financial.goal.domain.repository;

import com.financialmanajer.financial.goal.domain.model.Goal;
import com.financialmanajer.financial.goal.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.shared.application.dto.PaginatedResult;

import java.util.Optional;

public interface GoalRepository {
    boolean existsByNameAndUserId(String name, Long userId);
    Goal save(Goal goal);
    PaginatedResult<Goal, Void> findAllActiveByUserId(Long userId, GoalFilterDTO filter);
    Optional<Goal> findByIdAndUserId(Long id, Long userId);
    java.util.List<Goal> findAllActive();
    PaginatedResult<Goal, Void> findAllArchivedByUserId(Long userId, GoalFilterDTO filter);
}