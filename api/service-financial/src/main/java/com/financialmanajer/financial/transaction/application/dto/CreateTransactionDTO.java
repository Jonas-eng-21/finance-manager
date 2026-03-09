package com.financialmanajer.financial.transaction.application.dto;

import com.financialmanajer.financial.transaction.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionDTO(
        Long userId,
        TransactionType type,
        BigDecimal amount,
        Long categoryId,
        String description,
        LocalDate transactionDate,
        Long goalId
) {}