package com.financialmanajer.financial.application.dto;

import com.financialmanajer.financial.domain.model.TransactionType;

public record CreateCategoryDTO(Long userId, String name, TransactionType type) {
}