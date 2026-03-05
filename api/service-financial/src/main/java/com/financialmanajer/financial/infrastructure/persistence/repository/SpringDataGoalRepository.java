package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.infrastructure.persistence.entity.GoalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataGoalRepository extends JpaRepository<GoalEntity, Long> {

    boolean existsByNameIgnoreCaseAndUserIdAndDeletedAtIsNull(String name, Long userId);
    Page<GoalEntity> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);
}