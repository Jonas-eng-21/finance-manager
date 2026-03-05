package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.CreateGoalDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateGoalUseCase {

    private final GoalRepository goalRepository;

    public CreateGoalUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal execute(CreateGoalDTO dto) {
        if (goalRepository.existsByNameAndUserId(dto.name(), dto.userId())) {
            throw new DomainValidationException("goal.validation.name.duplicated");
        }

        Goal goal = new Goal(
                dto.userId(),
                dto.name(),
                dto.targetAmount(),
                dto.startDate(),
                dto.targetDate()
        );

        return goalRepository.save(goal);
    }
}