package com.financialmanajer.financial.transaction.domain.model;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlySummary(
        BigDecimal totalIncomes,
        BigDecimal totalExpenses,
        BigDecimal balance,
        Map<String, BigDecimal> expensesByCategory
) {
    public MonthlySummary {
        totalIncomes = totalIncomes == null ? BigDecimal.ZERO : totalIncomes;
        totalExpenses = totalExpenses == null ? BigDecimal.ZERO : totalExpenses;
        balance = totalIncomes.subtract(totalExpenses);
        expensesByCategory = expensesByCategory == null ? Map.of() : expensesByCategory;
    }
}