package com.financialmanajer.financial.transaction.application.dto;

import com.financialmanajer.financial.transaction.domain.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionFilterParams(
        LocalDate startDate,
        LocalDate endDate,
        TransactionType type,
        Long categoryId,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String sortBy,
        String sortDirection,
        Integer page,
        Integer size
) {
    public int getPageOrDefault() { return page != null ? page : 0; }
    public int getSizeOrDefault() { return size != null ? size : 10; }
}