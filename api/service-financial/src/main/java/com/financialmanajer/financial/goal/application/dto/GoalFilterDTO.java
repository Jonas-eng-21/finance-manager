package com.financialmanajer.financial.goal.application.dto;

public record GoalFilterDTO(
        int page,
        int size,
        String sortBy,
        String direction
) {}