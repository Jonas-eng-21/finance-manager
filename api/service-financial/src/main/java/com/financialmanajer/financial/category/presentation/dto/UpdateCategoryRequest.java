package com.financialmanajer.financial.category.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank(message = "{category.validation.name.required}")
        @Size(min = 3, max = 50, message = "{category.validation.name.size}")
        String name
) {}