package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.application.dto.TransactionSummary;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("transactionDate", "amount", "createdAt");

    public ListTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public PaginatedResult<Transaction, TransactionSummary> execute(TransactionFilterDTO filter) {

        if (filter.startDate() != null && filter.endDate() != null) {
            if (filter.startDate().isAfter(filter.endDate())) {
                throw new DomainValidationException("transaction.validation.invalid_date_range");
            }
        }

        if (filter.minAmount() != null && filter.minAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("transaction.validation.negative_amount");
        }

        if (filter.minAmount() != null && filter.maxAmount() != null) {
            if (filter.minAmount().compareTo(filter.maxAmount()) > 0) {
                throw new DomainValidationException("transaction.validation.invalid_amount_range");
            }
        }

        if (filter.sortBy() != null && !ALLOWED_SORT_FIELDS.contains(filter.sortBy())) {
            throw new DomainValidationException("transaction.validation.invalid_sort_field");
        }

        return transactionRepository.findByFilter(filter);
    }
}