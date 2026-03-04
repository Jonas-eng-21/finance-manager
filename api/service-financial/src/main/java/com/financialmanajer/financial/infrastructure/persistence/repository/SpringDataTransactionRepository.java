package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, Long> {
}