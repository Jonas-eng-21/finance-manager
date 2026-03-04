package com.financialmanajer.financial.application.usecase;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DeleteTransactionUseCase deleteTransactionUseCase;

    @Test
    @DisplayName("Deve realizar o soft delete com sucesso")
    void should_soft_delete_transaction_successfully() {
        Long userId = 1L;
        Long transactionId = 100L;
        Transaction transaction = new Transaction(userId, TransactionType.EXPENSE, new BigDecimal("50.00"), 10L, "Café", LocalDate.now());
        transaction.setId(transactionId);

        when(transactionRepository.findActiveByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(transaction));

        deleteTransactionUseCase.execute(transactionId, userId);

        assertTrue(transaction.isDeleted(), "A transação deve estar marcada como deletada");
        assertNotNull(transaction.getDeletedAt());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    @DisplayName("Deve retornar 404 (not_found) se tentar deletar transação inexistente, deletada ou de outro usuário")
    void should_fail_when_transaction_not_found_or_already_deleted() {
        Long userId = 1L;
        Long transactionId = 100L;

        when(transactionRepository.findActiveByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> deleteTransactionUseCase.execute(transactionId, userId));

        assertEquals("transaction.validation.not_found", ex.getMessage());
        verify(transactionRepository, never()).save(any());
    }
}