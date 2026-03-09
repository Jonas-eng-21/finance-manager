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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListArchivedGoalsUseCaseTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private ListArchivedGoalsUseCase listArchivedGoalsUseCase;

    @Test
    @DisplayName("Deve listar apenas metas arquivadas do usuário com paginação")
    void should_list_only_archived_goals() {
        Long userId = 1L;
        GoalFilterDTO filter = new GoalFilterDTO(0, 10, "targetDate", "desc");

        Goal archivedGoal = new Goal(userId, "Viagem 2025", new BigDecimal("5000.00"), LocalDate.now().minusMonths(6), LocalDate.now().minusDays(1));
        archivedGoal.archive();

        PaginatedResult<Goal, Void> mockResult = new PaginatedResult<>(
                List.of(archivedGoal), 0, 10, 1L, 1, null
        );

        when(goalRepository.findAllArchivedByUserId(userId, filter)).thenReturn(mockResult);

        PaginatedResult<Goal, Void> result = listArchivedGoalsUseCase.execute(userId, filter);

        assertEquals(1, result.content().size());
        assertEquals("Viagem 2025", result.content().getFirst().getName());
        verify(goalRepository, times(1)).findAllArchivedByUserId(userId, filter);
    }
}