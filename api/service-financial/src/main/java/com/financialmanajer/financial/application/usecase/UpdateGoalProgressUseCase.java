package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.UpdateGoalProgressDTO;
import com.financialmanajer.financial.domain.exception.ResourceNotFoundException;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateGoalProgressUseCase {

    private final GoalRepository goalRepository;

    public UpdateGoalProgressUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal execute(UpdateGoalProgressDTO dto) {
        Goal goal = goalRepository.findByIdAndUserId(dto.goalId(), dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("goal.not_found"));

        goal.addProgress(dto.amount());

        if (goal.isCompleted() && !goal.isArchived()) {
            goal.archive();
        }

        return goalRepository.save(goal);
    }
}