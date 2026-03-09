package com.financialmanajer.financial.transaction.domain.repository;

import com.financialmanajer.financial.transaction.domain.model.MonthlySummary;
import java.time.YearMonth;

public interface MonthlySummaryRepository {
    MonthlySummary getSummary(Long userId, YearMonth month);
}