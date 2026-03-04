package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.domain.model.Transaction;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
}