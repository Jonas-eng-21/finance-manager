package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.CreateTransactionDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.model.TransactionType;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private CreateTransactionUseCase createTransactionUseCase;

    @Test
    @DisplayName("Deve criar uma transação com sucesso quando a categoria for válida e do mesmo tipo")
    void should_create_transaction_successfully() {
        Long userId = 1L;
        Long categoryId = 10L;
        Category validCategory = new Category(userId, "Salário", TransactionType.INCOME);

        CreateTransactionDTO dto = new CreateTransactionDTO(
                userId, TransactionType.INCOME, new BigDecimal("5000.00"), categoryId, "Salário do mês", LocalDate.now(), null
        );

        when(categoryRepository.findActiveByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(validCategory));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        Transaction result = createTransactionUseCase.execute(dto);

        assertNotNull(result.getId());
        assertEquals(new BigDecimal("5000.00"), result.getAmount());
    }

    @Test
    @DisplayName("Deve falhar ao tentar usar uma categoria que não existe")
    void should_fail_when_category_not_found() {
        CreateTransactionDTO dto = new CreateTransactionDTO(
                1L, TransactionType.EXPENSE, new BigDecimal("100.0"), 99L, "Teste", LocalDate.now() , null
        );

        when(categoryRepository.findActiveByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> createTransactionUseCase.execute(dto));
        assertEquals("category.validation.not_found", ex.getMessage());
    }

    @Test
    @DisplayName("Deve retornar 404 (not_found) ao tentar usar categoria de outro usuário (Isolamento)")
    void should_fail_when_category_belongs_to_another_user() {
        Long intruderId = 2L;
        CreateTransactionDTO dto = new CreateTransactionDTO(
                intruderId, TransactionType.EXPENSE, new BigDecimal("100.0"), 10L, "Teste", LocalDate.now(), null
        );

        when(categoryRepository.findActiveByIdAndUserId(10L, intruderId)).thenReturn(Optional.empty());

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> createTransactionUseCase.execute(dto));
        assertEquals("category.validation.not_found", ex.getMessage()); // O teste agora espera 404!
    }

    @Test
    @DisplayName("Deve falhar se o tipo da transação for diferente do tipo da categoria")
    void should_fail_when_category_type_mismatch() {
        Long userId = 1L;
        Category incomeCategory = new Category(userId, "Salário", TransactionType.INCOME);

        CreateTransactionDTO dto = new CreateTransactionDTO(
                userId, TransactionType.EXPENSE, new BigDecimal("100.0"), 10L, "Teste", LocalDate.now(), null
        );

        when(categoryRepository.findActiveByIdAndUserId(10L, userId)).thenReturn(Optional.of(incomeCategory));

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> createTransactionUseCase.execute(dto));
        assertEquals("transaction.validation.category_type_mismatch", ex.getMessage());
    }

    @Test
    @DisplayName("Deve criar uma transação de INCOME e atualizar a meta vinculada")
    void should_create_income_transaction_and_update_goal() {
        CreateTransactionDTO dto = new CreateTransactionDTO(1L, TransactionType.INCOME, new BigDecimal("500.00"), 2L, "Bônus", LocalDate.now(), 10L);

        Category incomeCategory = new Category(1L, "Bônus", TransactionType.INCOME);
        when(categoryRepository.findActiveByIdAndUserId(2L, 1L)).thenReturn(Optional.of(incomeCategory));

        Goal goal = new Goal(1L, "Reserva", new BigDecimal("5000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        goal.setId(10L);
        goal.loadCurrentAmount(new BigDecimal("1000.00"));

        when(goalRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(goal));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(99L);
            return t;
        });

        Transaction createdTransaction = createTransactionUseCase.execute(dto);

        assertNotNull(createdTransaction);
        assertEquals(10L, createdTransaction.getGoalId());
        assertEquals(new BigDecimal("1500.00"), goal.getCurrentAmount());

        verify(goalRepository, times(1)).save(goal);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve falhar ao tentar vincular a uma meta que não existe ou é de outro usuário")
    void should_fail_when_linked_goal_not_found() {
        CreateTransactionDTO dto = new CreateTransactionDTO(1L, TransactionType.INCOME, new BigDecimal("500.00"), 2L, "Bônus", LocalDate.now(), 99L);

        Category incomeCategory = new Category(1L, "Bônus", TransactionType.INCOME);
        when(categoryRepository.findActiveByIdAndUserId(2L, 1L)).thenReturn(Optional.of(incomeCategory));

        when(goalRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> createTransactionUseCase.execute(dto));
        assertEquals("goal.not_found", ex.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}