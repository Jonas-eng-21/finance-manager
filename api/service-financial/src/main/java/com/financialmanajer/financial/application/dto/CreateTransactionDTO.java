package com.financialmanajer.financial.application.dto;

import com.financialmanajer.financial.domain.model.TransactionType;

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