package com.financialmanajer.financial.presentation.dto;

import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.model.TransactionType;

import java.time.LocalDateTime;

public record CategoryResponse(Long id, String name, TransactionType type, LocalDateTime createdAt) {

    public static CategoryResponse fromDomain(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getCreatedAt()
        );
    }
}