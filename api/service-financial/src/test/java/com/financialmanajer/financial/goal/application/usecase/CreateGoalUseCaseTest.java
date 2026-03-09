package com.financialmanajer.financial.goal.application.usecase;

import com.financialmanajer.financial.goal.application.dto.CreateGoalDTO;
import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGoalUseCaseTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private CreateGoalUseCase createGoalUseCase;

    @Test
    @DisplayName("Deve criar uma meta financeira com sucesso")
    void should_create_goal_successfully() {
        CreateGoalDTO dto = new CreateGoalDTO(
                1L, "Reserva de Emergência", new BigDecimal("10000.00"),
                LocalDate.now(), LocalDate.now().plusMonths(12)
        );

        when(goalRepository.existsByNameAndUserId(dto.name(), dto.userId())).thenReturn(false);
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Goal createdGoal = createGoalUseCase.execute(dto);

        assertNotNull(createdGoal);
        assertEquals("Reserva de Emergência", createdGoal.getName());
        assertEquals(new BigDecimal("10000.00"), createdGoal.getTargetAmount());
        verify(goalRepository, times(1)).existsByNameAndUserId(dto.name(), dto.userId());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar uma meta com nome já existente para o mesmo usuário")
    void should_fail_when_goal_name_already_exists() {
        CreateGoalDTO dto = new CreateGoalDTO(
                1L, "Viagem Japão", new BigDecimal("15000.00"),
                LocalDate.now(), LocalDate.now().plusMonths(24)
        );

        when(goalRepository.existsByNameAndUserId(dto.name(), dto.userId())).thenReturn(true);

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> createGoalUseCase.execute(dto));

        assertEquals("goal.validation.name.duplicated", ex.getMessage());
        verify(goalRepository, never()).save(any(Goal.class));
    }
}