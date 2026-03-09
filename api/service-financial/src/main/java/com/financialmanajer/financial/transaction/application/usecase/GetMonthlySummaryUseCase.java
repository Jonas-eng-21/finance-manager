package com.financialmanajer.financial.transaction.application.usecase;

import com.financialmanajer.financial.transaction.domain.model.MonthlySummary;
import com.financialmanajer.financial.transaction.domain.repository.MonthlySummaryRepository;

import java.time.YearMonth;

public class GetMonthlySummaryUseCase {

    private final MonthlySummaryRepository repository;

    public GetMonthlySummaryUseCase(MonthlySummaryRepository repository) {
        this.repository = repository;
    }

    public MonthlySummary execute(Long userId, YearMonth month) {
        return repository.getSummary(userId, month);
    }
}