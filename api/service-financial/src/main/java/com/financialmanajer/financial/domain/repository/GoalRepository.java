package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;

public interface GoalRepository {
    boolean existsByNameAndUserId(String name, Long userId);
    Goal save(Goal goal);
    PaginatedResult<Goal, Void> findAllActiveByUserId(Long userId, GoalFilterDTO filter);
}