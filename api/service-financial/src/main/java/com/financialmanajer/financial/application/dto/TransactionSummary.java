package com.financialmanajer.financial.application.dto;

import java.math.BigDecimal;

public record TransactionSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {}