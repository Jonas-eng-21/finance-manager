package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.UpdateGoalProgressDTO;
import com.financialmanajer.financial.domain.exception.ResourceNotFoundException;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateGoalProgressUseCaseTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private UpdateGoalProgressUseCase updateGoalProgressUseCase;

    @Test
    @DisplayName("Deve atualizar o progresso de uma meta existente e pertencente ao usuário")
    void should_update_goal_progress_successfully() {
        Long userId = 1L;
        Long goalId = 10L;
        UpdateGoalProgressDTO dto = new UpdateGoalProgressDTO(goalId, userId, new BigDecimal("500.00"));

        Goal existingGoal = new Goal(userId, "Reserva", new BigDecimal("10000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        existingGoal.setId(goalId);
        existingGoal.loadCurrentAmount(new BigDecimal("1000.00"));

        when(goalRepository.findByIdAndUserId(goalId, userId)).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Goal updatedGoal = updateGoalProgressUseCase.execute(dto);

        assertNotNull(updatedGoal);
        assertEquals(new BigDecimal("1500.00"), updatedGoal.getCurrentAmount()); // 1000 + 500

        verify(goalRepository, times(1)).findByIdAndUserId(goalId, userId);
        verify(goalRepository, times(1)).save(existingGoal);
    }

    @Test
    @DisplayName("Deve falhar ao tentar atualizar uma meta que não existe ou é de outro usuário")
    void should_fail_when_goal_not_found_or_belongs_to_another_user() {
        UpdateGoalProgressDTO dto = new UpdateGoalProgressDTO(99L, 1L, new BigDecimal("500.00"));

        when(goalRepository.findByIdAndUserId(dto.goalId(), dto.userId())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> updateGoalProgressUseCase.execute(dto));

        assertEquals("goal.not_found", ex.getMessage());
        verify(goalRepository, never()).save(any(Goal.class));
    }
}