package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import com.financialmanajer.financial.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final SpringDataTransactionRepository springDataRepository;

    public TransactionRepositoryImpl(SpringDataTransactionRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();

        entity.setUserId(transaction.getUserId());
        entity.setType(transaction.getType());
        entity.setAmount(transaction.getAmount());
        entity.setCategoryId(transaction.getCategoryId());
        entity.setDescription(transaction.getDescription());
        entity.setTransactionDate(transaction.getTransactionDate());
        entity.setCreatedAt(transaction.getCreatedAt());

        TransactionEntity savedEntity = springDataRepository.save(entity);

        transaction.setId(savedEntity.getId());
        return transaction;
    }
}