package com.financialmanajer.financial.goal.application.usecase;

import com.financialmanajer.financial.goal.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.shared.application.dto.PaginatedResult;
import com.financialmanajer.financial.goal.domain.model.Goal;
import com.financialmanajer.financial.goal.domain.repository.GoalRepository;

public class ListArchivedGoalsUseCase {

    private final GoalRepository goalRepository;

    public ListArchivedGoalsUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public PaginatedResult<Goal, Void> execute(Long userId, GoalFilterDTO filter) {
        return goalRepository.findAllArchivedByUserId(userId, filter);
    }
}