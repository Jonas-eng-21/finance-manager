package com.financialmanajer.financial.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "{category.validation.name.required}")
        String name
) {}