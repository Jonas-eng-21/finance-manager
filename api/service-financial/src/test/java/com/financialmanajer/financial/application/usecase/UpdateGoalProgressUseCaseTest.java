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
        assertEquals(new BigDecimal("1500.00"), updatedGoal.getCurrentAmount());

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

    @Test
    @DisplayName("Deve arquivar a meta automaticamente ao atingir 100% do valor alvo")
    void should_archive_goal_automatically_when_target_reached() {

        com.financialmanajer.financial.domain.model.Goal mockGoal = new com.financialmanajer.financial.domain.model.Goal(
                1L, "Viagem", new java.math.BigDecimal("1000.00"), java.time.LocalDate.now(), java.time.LocalDate.now().plusMonths(6)
        );
        mockGoal.setId(10L);
        mockGoal.loadCurrentAmount(new java.math.BigDecimal("800.00"));

        org.mockito.Mockito.when(goalRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(java.util.Optional.of(mockGoal));


        UpdateGoalProgressDTO dto =
                new UpdateGoalProgressDTO(10L, 1L, new java.math.BigDecimal("200.00"));
        updateGoalProgressUseCase.execute(dto);

        org.junit.jupiter.api.Assertions.assertTrue(mockGoal.isCompleted(), "A meta deveria estar concluída");
        org.junit.jupiter.api.Assertions.assertTrue(mockGoal.isArchived(), "A meta deveria ter sido arquivada automaticamente");
        org.junit.jupiter.api.Assertions.assertNotNull(mockGoal.getArchivedAt(), "A data de arquivamento não deve ser nula");

        org.mockito.Mockito.verify(goalRepository, org.mockito.Mockito.times(1)).save(mockGoal);
    }
}