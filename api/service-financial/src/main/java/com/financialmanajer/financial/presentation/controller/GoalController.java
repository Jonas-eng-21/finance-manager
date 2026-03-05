package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.dto.CreateGoalDTO;
import com.financialmanajer.financial.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.usecase.CreateGoalUseCase;
import com.financialmanajer.financial.application.usecase.ListGoalsUseCase;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.presentation.dto.CreateGoalRequest;
import com.financialmanajer.financial.presentation.dto.GoalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.financialmanajer.financial.application.usecase.UpdateGoalProgressUseCase;
import com.financialmanajer.financial.application.dto.UpdateGoalProgressDTO;
import com.financialmanajer.financial.presentation.dto.UpdateGoalProgressRequest;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final CreateGoalUseCase createGoalUseCase;
    private final ListGoalsUseCase listGoalsUseCase;
    private final UpdateGoalProgressUseCase updateGoalProgressUseCase;

    public GoalController(CreateGoalUseCase createGoalUseCase,
                          ListGoalsUseCase listGoalsUseCase,
                          UpdateGoalProgressUseCase updateGoalProgressUseCase) {
        this.createGoalUseCase = createGoalUseCase;
        this.listGoalsUseCase = listGoalsUseCase;
        this.updateGoalProgressUseCase = updateGoalProgressUseCase;
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

    @GetMapping
    public ResponseEntity<PaginatedResult<GoalResponse, Void>> listGoals(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "targetDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        GoalFilterDTO filter = new GoalFilterDTO(page, size, sortBy, direction);

        PaginatedResult<Goal, Void> domainResult = listGoalsUseCase.execute(userId, filter);

        List<GoalResponse> responseContent = domainResult.content().stream()
                .map(GoalResponse::fromDomain)
                .toList();

        PaginatedResult<GoalResponse, Void> response = new PaginatedResult<>(
                responseContent,
                domainResult.page(),
                domainResult.size(),
                domainResult.totalElements(),
                domainResult.totalPages(),
                null
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<GoalResponse> updateProgress(
            @PathVariable Long goalId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateGoalProgressRequest request) {

        UpdateGoalProgressDTO dto = new UpdateGoalProgressDTO(goalId, userId, request.amount());

        Goal updatedGoal = updateGoalProgressUseCase.execute(dto);

        return ResponseEntity.ok(GoalResponse.fromDomain(updatedGoal));
    }
}