package com.financialmanajer.financial.transaction.application.usecase;

import com.financialmanajer.financial.transaction.domain.model.MonthlySummary;
import com.financialmanajer.financial.transaction.domain.repository.MonthlySummaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMonthlySummaryUseCaseTest {

    @Mock
    private MonthlySummaryRepository repository;

    @InjectMocks
    private GetMonthlySummaryUseCase useCase;

    @Test
    @DisplayName("Deve retornar o resumo mensal calculando o saldo corretamente")
    void should_return_monthly_summary() {
        Long userId = 1L;
        YearMonth month = YearMonth.of(2026, 3);
        Map<String, BigDecimal> categories = Map.of("Alimentação", new BigDecimal("500.00"));

        MonthlySummary mockSummary = new MonthlySummary(
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                null,
                categories
        );

        when(repository.getSummary(userId, month)).thenReturn(mockSummary);

        MonthlySummary result = useCase.execute(userId, month);

        assertEquals(new BigDecimal("1500.00"), result.balance());
        assertEquals(1, result.expensesByCategory().size());
        verify(repository, times(1)).getSummary(userId, month);
    }
}