package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.usecase.*;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.model.GoalStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.financialmanajer.financial.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.financialmanajer.financial.application.dto.UpdateGoalProgressDTO;
import com.financialmanajer.financial.domain.exception.ResourceNotFoundException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;


import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateGoalUseCase createGoalUseCase;

    @Mock
    private ListGoalsUseCase listGoalsUseCase;

    @Mock
    private UpdateGoalProgressUseCase updateGoalProgressUseCase;

    @Mock
    private DeleteGoalUseCase deleteGoalUseCase;

    @Mock
    private ListArchivedGoalsUseCase listArchivedGoalsUseCase;

    @Mock
    private GetGoalStatisticsUseCase getGoalStatisticsUseCase;

    @InjectMocks
    private GoalController goalController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar 201 Created ao registrar uma meta válida")
    void should_return_201_when_goal_is_valid() throws Exception {
        Goal mockGoal = new Goal(1L, "Reserva de Emergência", new BigDecimal("12000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        mockGoal.setId(10L);

        when(createGoalUseCase.execute(any())).thenReturn(mockGoal);

        String jsonPayload = """
                {
                  "name": "Reserva de Emergência",
                  "targetAmount": 12000.00,
                  "startDate": "%s",
                  "targetDate": "%s"
                }
                """.formatted(LocalDate.now(), LocalDate.now().plusMonths(12));

        mockMvc.perform(post("/api/goals")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Reserva de Emergência"))
                .andExpect(jsonPath("$.targetAmount").value(12000.00))
                .andExpect(jsonPath("$.currentAmount").value(0.00))
                .andExpect(jsonPath("$.monthlyRequiredSaving").value(1000.00));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request se campos obrigatórios faltarem")
    void should_return_400_when_request_is_invalid() throws Exception {
        String emptyJson = "{}";

        mockMvc.perform(post("/api/goals")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar metas com paginação e cálculos de progresso retornando 200 OK")
    void should_list_goals_with_progress_and_pagination() throws Exception {
        Goal mockGoal = new Goal(1L, "Reserva", new BigDecimal("10000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        mockGoal.setId(100L);
        mockGoal.loadCurrentAmount(new BigDecimal("3500.00"));

        PaginatedResult<Goal, Void> mockResult = new PaginatedResult<>(List.of(mockGoal), 0, 10, 1, 1, null);

        when(listGoalsUseCase.execute(eq(1L), any(GoalFilterDTO.class))).thenReturn(mockResult);

        mockMvc.perform(get("/api/goals")
                        .header("X-User-Id", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(100))
                .andExpect(jsonPath("$.content[0].name").value("Reserva"))
                .andExpect(jsonPath("$.content[0].currentAmount").value(3500.00))
                .andExpect(jsonPath("$.content[0].remainingAmount").value(6500.00))
                .andExpect(jsonPath("$.content[0].progressPercentage").value(35.00))
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));
    }

    @org.springframework.web.bind.annotation.ControllerAdvice
    static class GlobalExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
        public org.springframework.http.ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
            return org.springframework.http.ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Deve atualizar o progresso da meta e retornar 200 OK")
    void should_update_progress_and_return_200() throws Exception {
        Goal mockGoal = new Goal(1L, "Reserva", new BigDecimal("10000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        mockGoal.setId(10L);
        mockGoal.addProgress(new BigDecimal("500.00"));

        when(updateGoalProgressUseCase.execute(any(UpdateGoalProgressDTO.class))).thenReturn(mockGoal);

        String jsonPayload = """
                {
                  "amount": 500.00
                }
                """;

        mockMvc.perform(patch("/api/goals/10/progress")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.currentAmount").value(500.00))
                .andExpect(jsonPath("$.progressPercentage").value(5.00));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found se a meta não existir")
    void should_return_404_when_goal_not_found() throws Exception {
        when(updateGoalProgressUseCase.execute(any(UpdateGoalProgressDTO.class)))
                .thenThrow(new ResourceNotFoundException("goal.not_found"));

        String jsonPayload = """
                {
                  "amount": 500.00
                }
                """;

        mockMvc.perform(patch("/api/goals/99/progress")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar a meta e retornar 204 No Content")
    void should_delete_goal_and_return_204() throws Exception {
        Long goalId = 1L;
        Long userId = 1L;

        org.mockito.Mockito.doNothing().when(deleteGoalUseCase).execute(goalId, userId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/goals/{id}", goalId)
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNoContent());

        org.mockito.Mockito.verify(deleteGoalUseCase, org.mockito.Mockito.times(1)).execute(goalId, userId);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar meta inexistente")
    void should_return_404_when_deleting_non_existent_goal() throws Exception {
        Long goalId = 99L;
        Long userId = 1L;

        org.mockito.Mockito.doThrow(new com.financialmanajer.financial.domain.exception.ResourceNotFoundException("goal.not_found"))
                .when(deleteGoalUseCase).execute(goalId, userId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/goals/{id}", goalId)
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao listar metas arquivadas")
    void should_return_200_when_listing_archived_goals() throws Exception {
        Long userId = 1L;

        org.mockito.Mockito.when(listArchivedGoalsUseCase.execute(org.mockito.ArgumentMatchers.eq(userId), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new com.financialmanajer.financial.application.dto.PaginatedResult<>(
                        java.util.List.of(), 0, 10, 0L, 0, null
                ));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/goals/archived")
                        .header("X-User-Id", String.valueOf(userId))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());

        org.mockito.Mockito.verify(listArchivedGoalsUseCase, org.mockito.Mockito.times(1))
                .execute(org.mockito.ArgumentMatchers.eq(userId), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Deve retornar as estatísticas de metas com status 200 OK")
    void should_return_goal_statistics_with_200_ok() throws Exception {
        Long userId = 1L;
        GoalStatistics stats = new GoalStatistics(10L, 5L, 5L, new java.math.BigDecimal("5000.00"), 30.5);

        org.mockito.Mockito.when(getGoalStatisticsUseCase.execute(userId)).thenReturn(stats);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/goals/statistics")
                        .header("X-User-Id", String.valueOf(userId)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.totalGoals").value(10))
                .andExpect(jsonPath("$.completedGoals").value(5))
                .andExpect(jsonPath("$.activeGoals").value(5))
                .andExpect(jsonPath("$.completionRate").value(50.0))
                .andExpect(jsonPath("$.totalSavedAmount").value(5000.0))
                .andExpect(jsonPath("$.averageCompletionDays").value(30));

        org.mockito.Mockito.verify(getGoalStatisticsUseCase, org.mockito.Mockito.times(1)).execute(userId);
    }
}