package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.domain.model.Transaction;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    PaginatedResult<Transaction> findByFilter(TransactionFilterDTO filter);
}