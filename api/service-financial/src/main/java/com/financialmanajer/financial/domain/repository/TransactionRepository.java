package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.application.dto.TransactionSummary;
import com.financialmanajer.financial.domain.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    PaginatedResult<Transaction, TransactionSummary> findByFilter(TransactionFilterDTO filter);
    Optional<Transaction> findActiveByIdAndUserId(Long id, Long userId);
}