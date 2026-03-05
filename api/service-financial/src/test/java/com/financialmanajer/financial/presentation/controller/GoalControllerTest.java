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

    @InjectMocks
    private GoalController goalController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();
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
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS")); // 👈 Validando a melhoria Sênior que fizemos!
    }
}