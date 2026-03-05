package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.dto.CreateGoalDTO;
import com.financialmanajer.financial.application.usecase.CreateGoalUseCase;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.presentation.dto.CreateGoalRequest;
import com.financialmanajer.financial.presentation.dto.GoalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final CreateGoalUseCase createGoalUseCase;

    public GoalController(CreateGoalUseCase createGoalUseCase) {
        this.createGoalUseCase = createGoalUseCase;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateGoalRequest request) {

        CreateGoalDTO dto = new CreateGoalDTO(
                userId,
                request.name(),
                request.targetAmount(),
                request.startDate(),
                request.targetDate()
        );

        Goal createdGoal = createGoalUseCase.execute(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(GoalResponse.fromDomain(createdGoal));
    }
}