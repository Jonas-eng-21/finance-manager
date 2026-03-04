package com.financialmanajer.financial.domain.model;

import com.financialmanajer.financial.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Deve criar uma transação válida")
    void should_create_valid_transaction() {
        Transaction transaction = new Transaction(
                1L,
                TransactionType.EXPENSE,
                new BigDecimal("150.75"),
                10L,
                "Supermercado",
                LocalDate.of(2026, 3, 3)
        );

        assertNotNull(transaction);
        assertEquals(new BigDecimal("150.75"), transaction.getAmount());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertNotNull(transaction.getCreatedAt());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-50.00", "-0.01"})
    @DisplayName("Deve lançar exceção se o valor for zero ou negativo")
    void should_throw_exception_when_amount_is_invalid(String amountStr) {
        BigDecimal invalidAmount = new BigDecimal(amountStr);

        DomainValidationException exception = assertThrows(DomainValidationException.class, () ->
                new Transaction(1L, TransactionType.INCOME, invalidAmount, 10L, "Salário", LocalDate.now())
        );

        assertEquals("transaction.validation.amount.must_be_positive", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se a descrição passar de 255 caracteres")
    void should_throw_exception_when_description_is_too_long() {
        String longDescription = "A".repeat(256);

        DomainValidationException exception = assertThrows(DomainValidationException.class, () ->
                new Transaction(1L, TransactionType.EXPENSE, new BigDecimal("10.0"), 10L, longDescription, LocalDate.now())
        );

        assertEquals("transaction.validation.description.too_long", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se campos obrigatórios forem nulos")
    void should_throw_exception_when_required_fields_are_null() {
        assertThrows(DomainValidationException.class, () ->
                new Transaction(null, TransactionType.EXPENSE, new BigDecimal("10.0"), 10L, "Desc", LocalDate.now())
        );
        assertThrows(DomainValidationException.class, () ->
                new Transaction(1L, null, new BigDecimal("10.0"), 10L, "Desc", LocalDate.now())
        );
        assertThrows(DomainValidationException.class, () ->
                new Transaction(1L, TransactionType.EXPENSE, null, 10L, "Desc", LocalDate.now())
        );
        assertThrows(DomainValidationException.class, () ->
                new Transaction(1L, TransactionType.EXPENSE, new BigDecimal("10.0"), null, "Desc", LocalDate.now())
        );
        assertThrows(DomainValidationException.class, () ->
                new Transaction(1L, TransactionType.EXPENSE, new BigDecimal("10.0"), 10L, "Desc", null)
        );
    }
}