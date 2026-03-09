package com.financialmanajer.financial.category.domain.model;

import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.transaction.domain.model.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    @DisplayName("Deve criar categoria com sucesso quando os dados são válidos")
    void should_create_category_with_valid_data() {
        Category category = new Category(1L, "Alimentação", TransactionType.EXPENSE);

        assertEquals(1L, category.getUserId());
        assertEquals("Alimentação", category.getName());
        assertNotNull(category.getCreatedAt());
    }

    @Test
    @DisplayName("Deve remover espaços em branco do início e fim do nome (Trim)")
    void should_trim_category_name() {
        Category category = new Category(1L, "  Transporte  ", TransactionType.EXPENSE);
        assertEquals("Transporte", category.getName());
    }

    @Test
    @DisplayName("Deve lançar exceção com chave de i18n se o nome for nulo ou vazio")
    void should_fail_if_name_is_null_or_empty() {
        DomainValidationException ex1 = assertThrows(DomainValidationException.class, () -> new Category(1L, null, TransactionType.EXPENSE));
        assertEquals("category.validation.name.required", ex1.getMessage());

        DomainValidationException ex2 = assertThrows(DomainValidationException.class, () -> new Category(1L, "   ", TransactionType.EXPENSE));
        assertEquals("category.validation.name.required", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção com chave de i18n se o nome for menor que 2 caracteres")
    void should_fail_if_name_is_less_than_2_characters() {
        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> new Category(1L, "A", TransactionType.EXPENSE));
        assertEquals("category.validation.name.min_length", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção com chave de i18n se o nome for maior que 50 caracteres")
    void should_fail_if_name_is_greater_than_50_characters() {
        String longName = "A".repeat(51);
        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> new Category(1L, longName, TransactionType.EXPENSE));
        assertEquals("category.validation.name.max_length", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção com chave de i18n se o user_id for nulo")
    void should_fail_if_user_id_is_null() {
        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> new Category(null, "Moradia", TransactionType.EXPENSE));
        assertEquals("category.validation.user_id.required", ex.getMessage());
    }

    @Test
    @DisplayName("Deve marcar a categoria como deletada (Soft Delete)")
    void should_mark_category_as_deleted() {
        Category category = new Category(1L, "Transporte", TransactionType.EXPENSE);

        assertFalse(category.isDeleted());
        assertNull(category.getDeletedAt());

        category.delete();

        assertTrue(category.isDeleted());
        assertNotNull(category.getDeletedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção com chave de i18n se o tipo (TransactionType) for nulo")
    void should_fail_if_type_is_null() {
        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> new Category(1L, "Lazer", null));
        assertEquals("transaction.validation.type.required", ex.getMessage());
    }
}