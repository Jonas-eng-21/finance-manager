package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.domain.model.Goal;

public interface GoalRepository {
    boolean existsByNameAndUserId(String name, Long userId);
    Goal save(Goal goal);
}