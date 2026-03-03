package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCategoriesUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ListCategoriesUseCase listCategoriesUseCase;

    @Test
    @DisplayName("Deve retornar lista de categorias do usuário")
    void should_return_list_of_categories_for_user() {
        // Arrange
        Long userId = 1L;
        List<Category> mockCategories = List.of(
                new Category(userId, "Alimentação"),
                new Category(userId, "Lazer")
        );
        when(categoryRepository.findAllByUserId(userId)).thenReturn(mockCategories);

        List<Category> result = listCategoriesUseCase.execute(userId);

        assertEquals(2, result.size());
        assertEquals("Alimentação", result.get(0).getName());
        assertEquals("Lazer", result.get(1).getName());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem categorias")
    void should_return_empty_list_when_no_categories() {
        Long userId = 1L;
        when(categoryRepository.findAllByUserId(userId)).thenReturn(List.of());

        List<Category> result = listCategoriesUseCase.execute(userId);

        assertTrue(result.isEmpty());
    }
}