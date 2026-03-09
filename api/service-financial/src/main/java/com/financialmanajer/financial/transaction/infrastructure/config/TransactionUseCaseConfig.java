package com.financialmanajer.financial.transaction.infrastructure.config;

import com.financialmanajer.financial.transaction.application.usecase.GetMonthlySummaryUseCase;
import com.financialmanajer.financial.transaction.domain.repository.MonthlySummaryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionUseCaseConfig {

    @Bean
    public GetMonthlySummaryUseCase getMonthlySummaryUseCase(MonthlySummaryRepository repository) {
        return new GetMonthlySummaryUseCase(repository);
    }
}