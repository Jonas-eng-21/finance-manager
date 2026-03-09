package com.financialmanajer.financial.transaction.presentation.dto;

import com.financialmanajer.financial.transaction.domain.model.TransactionType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
        TransactionType type,

        @Positive(message = "{transaction.validation.amount.positive}")
        BigDecimal amount,

        Long categoryId,

        @Size(max = 255, message = "{transaction.validation.description.too_long}")
        String description,

        LocalDate transactionDate
) {}