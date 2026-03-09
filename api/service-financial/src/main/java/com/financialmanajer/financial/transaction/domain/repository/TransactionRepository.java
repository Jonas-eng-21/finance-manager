package com.financialmanajer.financial.transaction.domain.repository;

import com.financialmanajer.financial.shared.application.dto.PaginatedResult;
import com.financialmanajer.financial.transaction.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.transaction.application.dto.TransactionSummary;
import com.financialmanajer.financial.transaction.domain.model.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    PaginatedResult<Transaction, TransactionSummary> findByFilter(TransactionFilterDTO filter);
    Optional<Transaction> findActiveByIdAndUserId(Long id, Long userId);
}