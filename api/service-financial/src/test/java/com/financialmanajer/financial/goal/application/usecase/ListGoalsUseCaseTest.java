package com.financialmanajer.financial.goal.application.usecase;

import com.financialmanajer.financial.goal.application.dto.GoalFilterDTO;
import com.financialmanajer.financial.shared.application.dto.PaginatedResult;
import com.financialmanajer.financial.goal.domain.model.Goal;
import com.financialmanajer.financial.goal.domain.repository.GoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListGoalsUseCaseTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private ListGoalsUseCase listGoalsUseCase;

    @Test
    @DisplayName("Deve listar as metas do usuário de forma paginada")
    void should_list_active_goals_for_user() {
        Long userId = 1L;
        GoalFilterDTO filter = new GoalFilterDTO( 0, 10, "targetDate", "asc");

        Goal goal = new Goal(userId, "Reserva de Emergência", new BigDecimal("10000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));

        PaginatedResult<Goal, Void> expectedResult = new PaginatedResult<>(
                List.of(goal), 0, 10, 1, 1, null
        );

        when(goalRepository.findAllActiveByUserId(userId, filter)).thenReturn(expectedResult);

        PaginatedResult<Goal, Void> result = listGoalsUseCase.execute(userId, filter);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("Reserva de Emergência", result.content().get(0).getName());

        verify(goalRepository, times(1)).findAllActiveByUserId(userId, filter);
    }
}