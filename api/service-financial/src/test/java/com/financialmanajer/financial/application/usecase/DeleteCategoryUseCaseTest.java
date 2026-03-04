package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.model.TransactionType;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @Test
    @DisplayName("Deve realizar o soft delete da categoria com sucesso")
    void should_soft_delete_category_successfully() {
        Long userId = 1L;
        Long catId = 100L;
        Category category = new Category(userId, "Lazer" , TransactionType.EXPENSE);

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));

        deleteCategoryUseCase.execute(catId, userId);

        assertTrue(category.isDeleted());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Deve falhar ao tentar excluir categoria de outro usuário (403)")
    void should_fail_when_deleting_others_category() {
        Long ownerId = 1L;
        Long intruderId = 2L;
        Category category = new Category(ownerId, "Salário" , TransactionType.EXPENSE);

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));

        assertThrows(DomainValidationException.class, () -> deleteCategoryUseCase.execute(100L, intruderId));
    }

    @Test
    @DisplayName("Não deve alterar nada se a categoria já estiver deletada (Idempotência)")
    void should_do_nothing_if_already_deleted() {
        Long userId = 1L;
        Category category = new Category(userId, "Antiga" , TransactionType.EXPENSE);
        category.delete();

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));

        deleteCategoryUseCase.execute(100L, userId);

        verify(categoryRepository, never()).save(any());
    }
}