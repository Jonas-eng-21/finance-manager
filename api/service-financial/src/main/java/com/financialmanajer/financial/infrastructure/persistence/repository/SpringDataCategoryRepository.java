package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByUserIdAndNameIgnoreCaseAndDeletedAtIsNull(Long userId, String name);

    List<CategoryEntity> findAllByUserIdAndDeletedAtIsNullOrderByNameAsc(Long userId);
}