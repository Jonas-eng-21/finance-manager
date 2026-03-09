package com.financialmanajer.financial.transaction.presentation.dto;

import com.financialmanajer.financial.transaction.domain.model.Transaction;
import com.financialmanajer.financial.transaction.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        TransactionType type,
        BigDecimal amount,
        Long categoryId,
        String description,
        LocalDate transactionDate,
        LocalDateTime createdAt,
        Long goalId
) {
    public static TransactionResponse fromDomain(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCategoryId(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getCreatedAt(),
                transaction.getGoalId()
        );
    }
}