package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.application.dto.TransactionSummary;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import com.financialmanajer.financial.infrastructure.persistence.entity.TransactionEntity;
import com.financialmanajer.financial.infrastructure.persistence.specification.TransactionSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import com.financialmanajer.financial.domain.model.TransactionType;
import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final SpringDataTransactionRepository springDataRepository;
    private final EntityManager entityManager;

    public TransactionRepositoryImpl(SpringDataTransactionRepository springDataRepository, EntityManager entityManager) {
        this.springDataRepository = springDataRepository;
        this.entityManager = entityManager;
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
        entity.setDeletedAt(transaction.getDeletedAt());

        TransactionEntity savedEntity = springDataRepository.save(entity);

        transaction.setId(savedEntity.getId());
        return transaction;
    }

    @Override
    public PaginatedResult<Transaction, TransactionSummary> findByFilter(TransactionFilterDTO filter) {
        Specification<TransactionEntity> spec = TransactionSpecification.withFilter(filter);

        String sortField = filter.sortBy() != null ? filter.sortBy() : "transactionDate";
        Sort.Direction direction = "ASC".equalsIgnoreCase(filter.sortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(filter.page(), filter.size(), Sort.by(direction, sortField).and(Sort.by(Sort.Direction.DESC, "createdAt")));

        Page<TransactionEntity> page = springDataRepository.findAll(spec, pageable);
        List<Transaction> content = page.getContent().stream().map(this::toDomain).toList();

        TransactionSummary summary = calculateSummary(spec);

        return new PaginatedResult<>(
                content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), summary
        );
    }

    private TransactionSummary calculateSummary(Specification<TransactionEntity> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<TransactionEntity> root = cq.from(TransactionEntity.class);

        Predicate predicate = spec.toPredicate(root, cq, cb);
        cq.where(predicate);

        cq.multiselect(root.get("type"), cb.sum(root.get("amount")));
        cq.groupBy(root.get("type"));

        List<Tuple> results = entityManager.createQuery(cq).getResultList();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Tuple t : results) {
            TransactionType type = t.get(0, TransactionType.class);
            BigDecimal sum = t.get(1, BigDecimal.class);

            if (sum == null) continue;

            if (type == TransactionType.INCOME) totalIncome = sum;
            else if (type == TransactionType.EXPENSE) totalExpense = sum;
        }

        return new TransactionSummary(totalIncome, totalExpense, totalIncome.subtract(totalExpense));
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
        transaction.loadDeletedAt(entity.getDeletedAt());

        return transaction;
    }

    @Override
    public Optional<Transaction> findActiveByIdAndUserId(Long id, Long userId) {
        return springDataRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .map(this::toDomain);
    }
}