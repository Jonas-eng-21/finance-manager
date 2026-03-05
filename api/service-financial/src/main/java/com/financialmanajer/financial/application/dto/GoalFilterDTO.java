package com.financialmanajer.financial.application.dto;

public record GoalFilterDTO(
        int page,
        int size,
        String sortBy,
        String direction
) {}