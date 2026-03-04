package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    public ListTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public PaginatedResult<Transaction> execute(TransactionFilterDTO filter) {

        if (filter.startDate() != null && filter.endDate() != null) {
            if (filter.startDate().isAfter(filter.endDate())) {
                throw new DomainValidationException("transaction.validation.invalid_date_range");
            }
        }

        return transactionRepository.findByFilter(filter);
    }
}