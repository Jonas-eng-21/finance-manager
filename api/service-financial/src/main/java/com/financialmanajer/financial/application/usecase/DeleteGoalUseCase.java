package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.domain.exception.ResourceNotFoundException;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.repository.GoalRepository;

public class DeleteGoalUseCase {

    private final GoalRepository goalRepository;

    public DeleteGoalUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public void execute(Long id, Long userId) {
        Goal goal = goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("goal.not_found"));

        goal.delete();

        goalRepository.save(goal);
    }
}