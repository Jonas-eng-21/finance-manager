package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.domain.model.GoalStatistics;
import com.financialmanajer.financial.domain.repository.GoalStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetGoalStatisticsUseCaseTest {

    @Mock
    private GoalStatisticsRepository statisticsRepository;

    @InjectMocks
    private GetGoalStatisticsUseCase getGoalStatisticsUseCase;

    @Test
    @DisplayName("Deve retornar as estatísticas do usuário corretamente")
    void should_return_statistics_for_user() {
        Long userId = 1L;
        GoalStatistics mockStats = new GoalStatistics(10L, 5L, 5L, new BigDecimal("5000.00"), 30.5);
        when(statisticsRepository.getStatisticsByUserId(userId)).thenReturn(mockStats);

        GoalStatistics result = getGoalStatisticsUseCase.execute(userId);

        assertEquals(10L, result.totalGoals());
        assertEquals(50.0, result.getCompletionRate());
        verify(statisticsRepository, times(1)).getStatisticsByUserId(userId);
    }
}