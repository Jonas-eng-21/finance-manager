package com.financialmanajer.financial.category.infrastructure.persistence.repository;

import com.financialmanajer.financial.category.domain.model.Category;
import com.financialmanajer.financial.transaction.domain.model.TransactionType;
import com.financialmanajer.financial.category.infrastructure.persistence.entity.CategoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
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
        Category category = new Category(1L, "Salário" , TransactionType.EXPENSE);

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
        entity.setType(TransactionType.EXPENSE);
        entity.setCreatedAt(LocalDateTime.now());
        springDataRepository.save(entity);

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
        c1.setUserId(user1);
        c1.setName("User 1 Cat");
        c1.setType(TransactionType.EXPENSE);
        c1.setCreatedAt(LocalDateTime.now());

        CategoryEntity c2 = new CategoryEntity();
        c2.setUserId(user2);
        c2.setName("User 2 Cat");
        c2.setType(TransactionType.EXPENSE);
        c2.setCreatedAt(LocalDateTime.now());

        springDataRepository.saveAll(List.of(c1, c2));

        List<Category> result = categoryRepository.findAllByUserId(user1);

        assertEquals(1, result.size());
        assertEquals("User 1 Cat", result.get(0).getName());
    }

    @Test
    @DisplayName("Não deve listar categorias deletadas e deve ignorá-las na verificação de existência")
    void should_ignore_soft_deleted_categories() {
        Long userId = 1L;

        CategoryEntity active = new CategoryEntity();
        active.setType(TransactionType.EXPENSE);
        active.setCreatedAt(LocalDateTime.now());
        active.setUserId(userId); active.setName("Ativa"); active.setCreatedAt(java.time.LocalDateTime.now());

        CategoryEntity deleted = new CategoryEntity();
        deleted.setType(TransactionType.EXPENSE);
        deleted.setCreatedAt(LocalDateTime.now());
        deleted.setUserId(userId); deleted.setName("Deletada"); deleted.setCreatedAt(java.time.LocalDateTime.now());
        deleted.setDeletedAt(java.time.LocalDateTime.now());

        springDataRepository.saveAll(List.of(active, deleted));

        List<Category> list = categoryRepository.findAllByUserId(userId);

        boolean existsActive = categoryRepository.existsByUserIdAndNameIgnoreCase(userId, "Ativa");
        boolean existsDeleted = categoryRepository.existsByUserIdAndNameIgnoreCase(userId, "Deletada");

        assertEquals(1, list.size(), "Deve trazer apenas 1 categoria (a ativa)");
        assertEquals("Ativa", list.get(0).getName());

        assertTrue(existsActive, "Deve encontrar a categoria ativa");
        assertFalse(existsDeleted, "A categoria deletada deve ser dada como inexistente para nova criação");
    }
}