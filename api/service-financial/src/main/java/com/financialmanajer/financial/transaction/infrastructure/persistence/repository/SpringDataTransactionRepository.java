package com.financialmanajer.financial.transaction.infrastructure.persistence.repository;

import com.financialmanajer.financial.transaction.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataTransactionRepository extends
        JpaRepository<TransactionEntity, Long>,
        JpaSpecificationExecutor<TransactionEntity> {

    Optional<TransactionEntity> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.userId = :userId AND t.type = 'INCOME' AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate AND t.deletedAt IS NULL")
    BigDecimal sumIncomes(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.userId = :userId AND t.type = 'EXPENSE' AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate AND t.deletedAt IS NULL")
    BigDecimal sumExpenses(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t.categoryId as categoryId, SUM(t.amount) as totalAmount FROM TransactionEntity t WHERE t.userId = :userId AND t.type = 'EXPENSE' AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate AND t.deletedAt IS NULL GROUP BY t.categoryId")
    List<CategoryExpenseProjection> getExpensesByCategoryId(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    interface CategoryExpenseProjection {
        Long getCategoryId();
        BigDecimal getTotalAmount();
    }
}