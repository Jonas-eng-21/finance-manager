package com.financialmanajer.financial.application.dto;

import com.financialmanajer.financial.domain.model.TransactionType;
import java.time.LocalDate;

public record TransactionFilterDTO(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        TransactionType type,
        Long categoryId,
        int page,
        int size
) {}