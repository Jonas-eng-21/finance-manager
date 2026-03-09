package com.financialmanajer.financial.transaction.infrastructure.persistence.repository;

import com.financialmanajer.financial.transaction.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SpringDataTransactionRepository extends
        JpaRepository<TransactionEntity, Long>,
        JpaSpecificationExecutor<TransactionEntity> {
    Optional<TransactionEntity> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}