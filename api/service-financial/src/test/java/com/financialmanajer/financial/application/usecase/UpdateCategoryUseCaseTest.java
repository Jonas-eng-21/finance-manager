package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.UpdateCategoryDTO;
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
class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private UpdateCategoryUseCase updateCategoryUseCase;

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void should_update_category_successfully() {
        Long userId = 1L;
        Long catId = 100L;
        Category category = new Category(userId, "Antigo Nome" , TransactionType.EXPENSE);
        category.setId(catId);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(catId, userId, "Novo Nome");

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByUserIdAndNameIgnoreCase(userId, "Novo Nome")).thenReturn(false);

        updateCategoryUseCase.execute(dto);

        assertEquals("Novo Nome", category.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar editar categoria de outro usuário")
    void should_throw_exception_when_editing_others_category() {
        Long ownerId = 1L;
        Long intruderId = 2L;
        Category category = new Category(ownerId, "Alimentação" , TransactionType.EXPENSE);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(100L, intruderId, "Ataque");

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));

        assertThrows(DomainValidationException.class, () -> updateCategoryUseCase.execute(dto));
    }

    @Test
    @DisplayName("Deve permitir atualizar se o nome for exatamente igual ao atual (idempotência)")
    void should_allow_update_when_name_is_same_as_current() {
        Long userId = 1L;
        Long catId = 100L;
        Category category = new Category(userId, "Saúde" , TransactionType.EXPENSE);
        category.setId(catId);

        UpdateCategoryDTO dto = new UpdateCategoryDTO(catId, userId, "  Saúde  ");

        when(categoryRepository.findById(catId)).thenReturn(java.util.Optional.of(category));

        updateCategoryUseCase.execute(dto);

        assertEquals("Saúde", category.getName());
        verify(categoryRepository, times(1)).save(category);
    }

}