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

    @Mock
    private com.financialmanajer.financial.domain.repository.GoalRepository goalRepository;

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

    @Test
    @DisplayName("Deve deletar uma transação vinculada e estornar o valor da meta")
    void should_delete_linked_transaction_and_rollback_goal() {
        Transaction mockTx = new Transaction(1L, com.financialmanajer.financial.domain.model.TransactionType.INCOME, new BigDecimal("500.00"), 2L, "Bônus", LocalDate.now());
        mockTx.setId(100L);
        mockTx.linkToGoal(10L);

        com.financialmanajer.financial.domain.model.Goal mockGoal = new com.financialmanajer.financial.domain.model.Goal(1L, "Reserva", new BigDecimal("5000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        mockGoal.setId(10L);
        mockGoal.loadCurrentAmount(new BigDecimal("1000.00"));

        when(transactionRepository.findActiveByIdAndUserId(100L, 1L)).thenReturn(java.util.Optional.of(mockTx));
        when(goalRepository.findByIdAndUserId(10L, 1L)).thenReturn(java.util.Optional.of(mockGoal));

        deleteTransactionUseCase.execute(100L, 1L);

        assertTrue(mockTx.isDeleted());
        assertEquals(new BigDecimal("500.00"), mockGoal.getCurrentAmount());

        verify(goalRepository, times(1)).save(mockGoal);
        verify(transactionRepository, times(1)).save(mockTx);
    }
}