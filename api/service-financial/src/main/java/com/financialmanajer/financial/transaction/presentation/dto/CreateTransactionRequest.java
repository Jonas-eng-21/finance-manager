package com.financialmanajer.financial.transaction.presentation.dto;

import com.financialmanajer.financial.transaction.domain.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        @NotNull(message = "{transaction.validation.type.required}")
        TransactionType type,

        @NotNull(message = "{transaction.validation.amount.required}")
        @Positive(message = "{transaction.validation.amount.must_be_positive}")
        BigDecimal amount,

        @NotNull(message = "{transaction.validation.category_id.required}")
        Long categoryId,

        @Size(max = 255, message = "{transaction.validation.description.too_long}")
        String description,

        @NotNull(message = "{transaction.validation.date.required}")
        LocalDate transactionDate,

        Long goalId
) {}