package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.UpdateTransactionDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.model.TransactionType;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private UpdateTransactionUseCase updateTransactionUseCase;

    @Test
    @DisplayName("Deve atualizar transação com sucesso (PATCH parcial)")
    void should_update_transaction_successfully() {
        Long userId = 1L;
        Transaction existingTx = new Transaction(userId, TransactionType.EXPENSE, new BigDecimal("100.00"), 10L, "Antiga", LocalDate.now());

        UpdateTransactionDTO dto = new UpdateTransactionDTO(
                1L, userId, null, new BigDecimal("150.00"), null, "Nova", null
        );

        when(transactionRepository.findActiveByIdAndUserId(1L, userId)).thenReturn(Optional.of(existingTx));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction updatedTx = updateTransactionUseCase.execute(dto);

        assertEquals(new BigDecimal("150.00"), updatedTx.getAmount());
        assertEquals("Nova", updatedTx.getDescription());
        assertEquals(TransactionType.EXPENSE, updatedTx.getType());
        assertEquals(10L, updatedTx.getCategoryId());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar transação inexistente ou de outro usuário")
    void should_fail_if_transaction_not_found() {
        UpdateTransactionDTO dto = new UpdateTransactionDTO(1L, 2L, null, null, null, null, null);

        when(transactionRepository.findActiveByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> updateTransactionUseCase.execute(dto));
        assertEquals("transaction.validation.not_found", ex.getMessage());
    }

    @Test
    @DisplayName("Deve validar e cruzar tipo ao atualizar a Categoria")
    void should_fail_if_new_category_is_invalid() {
        Long userId = 1L;
        Transaction existingTx = new Transaction(userId, TransactionType.EXPENSE, new BigDecimal("100.00"), 10L, "Antiga", LocalDate.now());

        UpdateTransactionDTO dto = new UpdateTransactionDTO(1L, userId, null, null, 20L, null, null);

        Category newCategory = new Category(userId, "Venda", TransactionType.INCOME);

        when(transactionRepository.findActiveByIdAndUserId(1L, userId)).thenReturn(Optional.of(existingTx));
        when(categoryRepository.findActiveByIdAndUserId(20L, userId)).thenReturn(Optional.of(newCategory));

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> updateTransactionUseCase.execute(dto));
        assertEquals("transaction.validation.category_type_mismatch", ex.getMessage());
    }
}