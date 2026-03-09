package com.financialmanajer.financial.category.application.dto;

import com.financialmanajer.financial.transaction.domain.model.TransactionType;

public record CreateCategoryDTO(Long userId, String name, TransactionType type) {
}