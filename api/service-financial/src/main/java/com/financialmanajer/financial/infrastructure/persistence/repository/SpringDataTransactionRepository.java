package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataTransactionRepository extends
        JpaRepository<TransactionEntity, Long>,
        JpaSpecificationExecutor<TransactionEntity> {
}