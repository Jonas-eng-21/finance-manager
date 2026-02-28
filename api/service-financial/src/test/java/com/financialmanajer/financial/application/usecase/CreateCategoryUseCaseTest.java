package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.CreateCategoryDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CreateCategoryUseCase createCategoryUseCase;

    @Test
    @DisplayName("Deve criar categoria com sucesso quando não existe duplicidade")
    void should_create_category_successfully() {
        CreateCategoryDTO dto = new CreateCategoryDTO(1L, "Alimentação");

        when(categoryRepository.existsByUserIdAndNameIgnoreCase(1L, "Alimentação")).thenReturn(false);

        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setId(100L);
            return savedCategory;
        });

        Category result = createCategoryUseCase.execute(dto);

        assertNotNull(result.getId());
        assertEquals("Alimentação", result.getName());
        assertEquals(1L, result.getUserId());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a categoria já existe para o usuário")
    void should_throw_exception_when_category_already_exists() {
        CreateCategoryDTO dto = new CreateCategoryDTO(1L, "Transporte");

        when(categoryRepository.existsByUserIdAndNameIgnoreCase(1L, "Transporte")).thenReturn(true);

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> {
            createCategoryUseCase.execute(dto);
        });

        assertEquals("category.validation.name.already_exists", ex.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }
}
