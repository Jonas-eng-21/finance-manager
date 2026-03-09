package com.financialmanajer.financial.goal.application.usecase;

import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.shared.domain.exception.ResourceNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGoalUseCaseTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private DeleteGoalUseCase deleteGoalUseCase;

    @Test
    @DisplayName("Deve deletar uma meta com sucesso (Soft Delete)")
    void should_delete_goal_successfully() {
        Goal mockGoal = new Goal(1L, "Reserva", new BigDecimal("5000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        when(goalRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(mockGoal));

        deleteGoalUseCase.execute(10L, 1L);

        assertTrue(mockGoal.isDeleted());
        verify(goalRepository, times(1)).save(mockGoal);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se a meta não existir ou for de outro usuário")
    void should_throw_exception_when_goal_not_found() {
        when(goalRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            deleteGoalUseCase.execute(10L, 1L);
        });

        assertEquals("goal.not_found", exception.getMessage());
        verify(goalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException se a meta já estiver deletada")
    void should_throw_exception_when_goal_is_already_deleted() {
        Goal mockGoal = new Goal(1L, "Reserva", new BigDecimal("5000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        mockGoal.delete();

        when(goalRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(mockGoal));

        DomainValidationException exception = assertThrows(DomainValidationException.class, () -> {
            deleteGoalUseCase.execute(10L, 1L);
        });

        assertEquals("goal.already_deleted", exception.getMessage());
        verify(goalRepository, never()).save(any());
    }
}