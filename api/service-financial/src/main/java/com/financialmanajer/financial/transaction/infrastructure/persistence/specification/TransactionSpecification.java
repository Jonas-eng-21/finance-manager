package com.financialmanajer.financial.transaction.infrastructure.persistence.specification;

import com.financialmanajer.financial.transaction.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.transaction.infrastructure.persistence.entity.TransactionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<TransactionEntity> withFilter(TransactionFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("userId"), filter.userId()));

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.startDate()));
            }

            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.endDate()));
            }

            if (filter.type() != null) {
                predicates.add(cb.equal(root.get("type"), filter.type()));
            }

            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), filter.categoryId()));
            }

            predicates.add(cb.equal(root.get("userId"), filter.userId()));
            predicates.add(cb.isNull(root.get("deletedAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}