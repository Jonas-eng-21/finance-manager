package com.financialmanajer.financial.goal.application.usecase;

import com.financialmanajer.financial.goal.application.event.GoalDeadlineApproachingEvent;
import com.financialmanajer.financial.goal.application.port.GoalAlertPublisher;
import com.financialmanajer.financial.goal.domain.model.Goal;
import com.financialmanajer.financial.goal.domain.repository.GoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckGoalsDeadlineUseCaseTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalAlertPublisher goalAlertPublisher;

    @InjectMocks
    private CheckGoalsDeadlineUseCase checkGoalsDeadlineUseCase;

    @Test
    @DisplayName("Deve publicar evento para metas ativas, não concluídas e próximas do prazo")
    void should_publish_event_when_goal_is_near_deadline() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(5);

        Goal goal = new Goal(1L, "Reserva de Emergência", new BigDecimal("10000.00"), today.minusMonths(1), deadline);
        goal.setId(10L);
        goal.loadCurrentAmount(new BigDecimal("8000.00"));

        when(goalRepository.findAllActive()).thenReturn(List.of(goal));

        checkGoalsDeadlineUseCase.execute(today);

        ArgumentCaptor<GoalDeadlineApproachingEvent> eventCaptor = ArgumentCaptor.forClass(GoalDeadlineApproachingEvent.class);
        verify(goalAlertPublisher, times(1)).publish(eventCaptor.capture());

        GoalDeadlineApproachingEvent firedEvent = eventCaptor.getValue();
        assertEquals(10L, firedEvent.goalId());
        assertEquals(1L, firedEvent.userId());
        assertEquals("Reserva de Emergência", firedEvent.goalName());
        assertEquals(new BigDecimal("2000.00"), firedEvent.remainingAmount());
        assertEquals(5L, firedEvent.daysRemaining());
    }

    @Test
    @DisplayName("Não deve publicar evento se a meta já estiver concluída")
    void should_not_publish_event_if_goal_is_completed() {

        LocalDate today = LocalDate.now();
        LocalDate deadline = today.plusDays(5);

        Goal goal = new Goal(1L, "Reserva de Emergência", new BigDecimal("10000.00"), today.minusMonths(1), deadline);
        goal.setId(10L);
        goal.loadCurrentAmount(new BigDecimal("10000.00"));

        when(goalRepository.findAllActive()).thenReturn(List.of(goal));


        checkGoalsDeadlineUseCase.execute(today);

        verify(goalAlertPublisher, never()).publish(any());
    }
}