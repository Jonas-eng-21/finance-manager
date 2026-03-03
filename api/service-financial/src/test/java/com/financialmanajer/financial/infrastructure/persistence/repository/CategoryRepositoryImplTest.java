package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.infrastructure.persistence.entity.CategoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(CategoryRepositoryImpl.class)
class CategoryRepositoryImplTest {

    @Autowired
    private CategoryRepositoryImpl categoryRepository;

    @Autowired
    private SpringDataCategoryRepository springDataRepository;

    @Test
    @DisplayName("Deve salvar a categoria no banco de dados e retornar com ID preenchido")
    void should_save_category() {
        Category category = new Category(1L, "Salário");

        Category saved = categoryRepository.save(category);

        assertNotNull(saved.getId(), "O ID não pode ser nulo após salvar");
        assertEquals("Salário", saved.getName());
        assertEquals(1L, saved.getUserId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Deve retornar true se a categoria já existir para o usuário (case-insensitive)")
    void should_return_true_when_category_exists() {
        CategoryEntity entity = new CategoryEntity();
        entity.setUserId(2L);
        entity.setName("Investimentos");
        entity.setCreatedAt(java.time.LocalDateTime.now());

        springDataRepository.save(entity);

        assertTrue(categoryRepository.existsByUserIdAndNameIgnoreCase(2L, "investimentos"));
        assertTrue(categoryRepository.existsByUserIdAndNameIgnoreCase(2L, "INVESTIMENTOS"));
        assertFalse(categoryRepository.existsByUserIdAndNameIgnoreCase(1L, "Investimentos"));
        assertFalse(categoryRepository.existsByUserIdAndNameIgnoreCase(2L, "Lazer"));
    }

    @Test
    @DisplayName("Deve listar apenas as categorias do usuário específico")
    void should_list_only_user_categories() {
        Long user1 = 1L;
        Long user2 = 2L;

        CategoryEntity c1 = new CategoryEntity();
        c1.setUserId(user1); c1.setName("User 1 Cat"); c1.setCreatedAt(java.time.LocalDateTime.now());

        CategoryEntity c2 = new CategoryEntity();
        c2.setUserId(user2); c2.setName("User 2 Cat"); c2.setCreatedAt(java.time.LocalDateTime.now());

        springDataRepository.saveAll(List.of(c1, c2));

        List<Category> result = categoryRepository.findAllByUserId(user1);

        assertEquals(1, result.size());
        assertEquals("User 1 Cat", result.get(0).getName());
    }
}