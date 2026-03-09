package com.financialmanajer.financial.transaction.presentation.dto;

import com.financialmanajer.financial.transaction.domain.model.MonthlySummary;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlySummaryResponse(
        BigDecimal totalIncomes,
        BigDecimal totalExpenses,
        BigDecimal balance,
        Map<String, BigDecimal> expensesByCategory
) {
    public static MonthlySummaryResponse fromDomain(MonthlySummary summary) {
        return new MonthlySummaryResponse(
                summary.totalIncomes(),
                summary.totalExpenses(),
                summary.balance(),
                summary.expensesByCategory()
        );
    }
}