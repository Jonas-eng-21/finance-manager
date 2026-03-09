package com.financialmanajer.financial.goal.infrastructure.persistence.repository;

import com.financialmanajer.financial.goal.infrastructure.persistence.entity.GoalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataGoalRepository extends JpaRepository<GoalEntity, Long> {

    boolean existsByNameIgnoreCaseAndUserIdAndDeletedAtIsNull(String name, Long userId);
    Page<GoalEntity> findByUserIdAndDeletedAtIsNullAndArchivedAtIsNull(Long userId, Pageable pageable);
    Optional<GoalEntity> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
    List<GoalEntity> findByDeletedAtIsNullAndArchivedAtIsNull();
    Page<GoalEntity> findByUserIdAndDeletedAtIsNullAndArchivedAtIsNotNull(Long userId, Pageable pageable);
}