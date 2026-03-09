package com.financialmanajer.financial.shared.application.dto;

import java.util.List;

public record PaginatedResult<T, S>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        S summary
) {}