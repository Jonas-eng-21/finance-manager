package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.application.dto.TransactionSummary;
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
    @DisplayName("Deve buscar transações com sumário chamando o repositório corretamente")
    void should_list_transactions_successfully() {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L, null, null, null, null, null, null, null, null, 0, 10
        );

        Transaction mockTx = new Transaction(1L, TransactionType.EXPENSE, new BigDecimal("100"), 1L, "Teste", LocalDate.now());
        TransactionSummary summary = new TransactionSummary(BigDecimal.ZERO, new BigDecimal("100"), new BigDecimal("-100"));
        PaginatedResult<Transaction, TransactionSummary> mockResult = new PaginatedResult<>(List.of(mockTx), 0, 10, 1, 1, summary);

        when(transactionRepository.findByFilter(filter)).thenReturn(mockResult);

        PaginatedResult<Transaction, TransactionSummary> result = listTransactionsUseCase.execute(filter);

        assertNotNull(result);
        assertNotNull(result.summary());
        assertEquals(new BigDecimal("-100"), result.summary().balance());
        verify(transactionRepository, times(1)).findByFilter(filter);
    }

    @Test
    @DisplayName("Deve falhar se a data de início for maior que a data de fim")
    void should_fail_if_start_date_is_after_end_date() {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L, LocalDate.now().plusDays(1), LocalDate.now().minusDays(1),
                null, null, null, null, null, null, 0, 10
        );

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> listTransactionsUseCase.execute(filter));
        assertEquals("transaction.validation.invalid_date_range", ex.getMessage());
        verify(transactionRepository, never()).findByFilter(any());
    }

    @Test
    @DisplayName("Deve falhar se minAmount for negativo")
    void should_fail_if_min_amount_is_negative() {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L, null, null, null, null, new BigDecimal("-10.0"), null, null, null, 0, 10
        );

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> listTransactionsUseCase.execute(filter));
        assertEquals("transaction.validation.negative_amount", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se minAmount for maior que maxAmount")
    void should_fail_if_min_amount_is_greater_than_max_amount() {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L, null, null, null, null, new BigDecimal("500.0"), new BigDecimal("100.0"), null, null, 0, 10
        );

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> listTransactionsUseCase.execute(filter));
        assertEquals("transaction.validation.invalid_amount_range", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se o campo de ordenação for inválido (Proteção contra SQL Injection)")
    void should_fail_if_sort_field_is_invalid() {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                1L, null, null, null, null, null, null, "senha_do_banco", "ASC", 0, 10
        );

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> listTransactionsUseCase.execute(filter));
        assertEquals("transaction.validation.invalid_sort_field", ex.getMessage());
    }
}