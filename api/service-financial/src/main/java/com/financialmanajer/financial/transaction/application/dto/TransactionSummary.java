package com.financialmanajer.financial.transaction.application.dto;

import java.math.BigDecimal;

public record TransactionSummary(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {}