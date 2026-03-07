package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;

import java.util.Optional;

public interface GoalRepository {
    boolean existsByNameAndUserId(String name, Long userId);
    Goal save(Goal goal);
    PaginatedResult<Goal, Void> findAllActiveByUserId(Long userId, GoalFilterDTO filter);
    Optional<Goal> findByIdAndUserId(Long id, Long userId);
    java.util.List<com.financialmanajer.financial.domain.model.Goal> findAllActive();
    PaginatedResult<Goal, Void> findAllArchivedByUserId(Long userId, GoalFilterDTO filter);
}