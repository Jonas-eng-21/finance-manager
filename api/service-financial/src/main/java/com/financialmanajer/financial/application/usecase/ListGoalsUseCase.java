package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import org.springframework.stereotype.Service;

@Service
public class ListGoalsUseCase {

    private final GoalRepository goalRepository;

    public ListGoalsUseCase(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public PaginatedResult<Goal, Void> execute(Long userId, GoalFilterDTO filter) {
        return goalRepository.findAllActiveByUserId(userId, filter);
    }
}