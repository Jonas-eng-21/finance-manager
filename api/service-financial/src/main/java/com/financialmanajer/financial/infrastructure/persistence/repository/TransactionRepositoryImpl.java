package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import com.financialmanajer.financial.infrastructure.persistence.entity.TransactionEntity;
import com.financialmanajer.financial.infrastructure.persistence.specification.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public PaginatedResult<Transaction> findByFilter(TransactionFilterDTO filter) {
        Pageable pageable = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.by(Sort.Direction.DESC, "transactionDate").and(Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        Page<TransactionEntity> page = springDataRepository.findAll(
                TransactionSpecification.withFilter(filter), pageable
        );

        List<Transaction> content = page.getContent().stream()
                .map(this::toDomain)
                .toList();

        return new PaginatedResult<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Transaction toDomain(TransactionEntity entity) {
        Transaction transaction = new Transaction(
                entity.getUserId(),
                entity.getType(),
                entity.getAmount(),
                entity.getCategoryId(),
                entity.getDescription(),
                entity.getTransactionDate()
        );
        transaction.setId(entity.getId());

        return transaction;
    }
}