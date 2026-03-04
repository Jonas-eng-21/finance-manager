package com.financialmanajer.financial.application.dto;

import com.financialmanajer.financial.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionFilterDTO(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        TransactionType type,
        Long categoryId,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String sortBy,
        String sortDirection,
        int page,
        int size
) {}