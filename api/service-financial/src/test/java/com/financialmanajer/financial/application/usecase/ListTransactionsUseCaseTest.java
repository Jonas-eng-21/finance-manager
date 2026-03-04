package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.model.TransactionType;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListTransactionsUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ListTransactionsUseCase listTransactionsUseCase;

    @Test
    @DisplayName("Deve buscar as transações chamando o repositório corretamente")
    void should_list_transactions_successfully() {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L, null, null, null, null, 0, 10
        );

        Transaction mockTx = new Transaction(1L, TransactionType.EXPENSE, new BigDecimal("100"), 1L, "Teste", LocalDate.now());
        PaginatedResult<Transaction> mockResult = new PaginatedResult<>(List.of(mockTx), 0, 10, 1, 1);

        when(transactionRepository.findByFilter(filter)).thenReturn(mockResult);

        PaginatedResult<Transaction> result = listTransactionsUseCase.execute(filter);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1L, result.totalElements());
        verify(transactionRepository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve falhar se a data de início for maior que a data de fim")
    void should_fail_if_start_date_is_after_end_date() {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L,
                LocalDate.now().plusDays(1),
                LocalDate.now().minusDays(1),
                null, null, 0, 10
        );

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> listTransactionsUseCase.execute(filter));
        assertEquals("transaction.validation.invalid_date_range", ex.getMessage());

        verify(transactionRepository, never()).findByFilter(any());
    }
}