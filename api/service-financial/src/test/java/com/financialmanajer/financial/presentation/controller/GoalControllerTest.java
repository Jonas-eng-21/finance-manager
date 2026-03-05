package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.usecase.CreateGoalUseCase;
import com.financialmanajer.financial.domain.model.Goal;
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
import com.financialmanajer.financial.application.usecase.ListGoalsUseCase;
import com.financialmanajer.financial.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.financialmanajer.financial.application.dto.UpdateGoalProgressDTO;
import com.financialmanajer.financial.application.usecase.UpdateGoalProgressUseCase;
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
}