package com.financialmanajer.financial.presentation.dto;

import com.financialmanajer.financial.domain.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "{category.validation.name.required}")
        @Size(min = 2, message = "{category.validation.name.min_length}")
        @Size(max = 50, message = "{category.validation.name.max_length}")
        String name,

        @NotNull(message = "{transaction.validation.type.required}")
        TransactionType type
) {}