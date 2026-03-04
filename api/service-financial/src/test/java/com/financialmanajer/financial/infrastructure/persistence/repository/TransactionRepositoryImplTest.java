package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.model.TransactionType;
import com.financialmanajer.financial.infrastructure.persistence.entity.TransactionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.financialmanajer.financial.application.dto.TransactionSummary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@Import(TransactionRepositoryImpl.class)
class TransactionRepositoryImplTest {

    @Autowired
    private TransactionRepositoryImpl transactionRepository;

    @Autowired
    private SpringDataTransactionRepository springDataRepository;

    @Test
    @DisplayName("Deve filtrar transações dinamicamente respeitando o isolamento do usuário")
    void should_filter_transactions_dynamically() {
        Long userId = 1L;
        Long intruderId = 2L;

        createEntity(userId, TransactionType.EXPENSE, "100.00", 10L, LocalDate.of(2026, 1, 15));
        createEntity(userId, TransactionType.INCOME, "5000.00", 11L, LocalDate.of(2026, 1, 20));
        createEntity(userId, TransactionType.EXPENSE, "50.00", 10L, LocalDate.of(2026, 2, 10));
        createEntity(intruderId, TransactionType.EXPENSE, "999.00", 10L, LocalDate.of(2026, 1, 15));

        TransactionFilterDTO filter1 = new TransactionFilterDTO(
                userId,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                TransactionType.EXPENSE,
                null,
                null, null, null, null,
                0, 10
        );
        PaginatedResult<Transaction, TransactionSummary> result1 = transactionRepository.findByFilter(filter1);

        assertEquals(1, result1.totalElements());
        assertEquals(new BigDecimal("100.00"), result1.content().get(0).getAmount());

        TransactionFilterDTO filter2 = new TransactionFilterDTO(
                userId, null, null, null, 10L,
                null, null, null, null,
                0, 10
        );
        PaginatedResult<Transaction, TransactionSummary> result2 = transactionRepository.findByFilter(filter2);
        assertEquals(2, result2.totalElements());
    }

    private void createEntity(Long userId, TransactionType type, String amount, Long catId, LocalDate date) {
        TransactionEntity entity = new TransactionEntity();
        entity.setUserId(userId);
        entity.setType(type);
        entity.setAmount(new BigDecimal(amount));
        entity.setCategoryId(catId);
        entity.setTransactionDate(date);
        entity.setCreatedAt(LocalDateTime.now());
        springDataRepository.save(entity);
    }
}