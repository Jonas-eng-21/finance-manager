package com.financialmanajer.financial.category.infrastructure.persistence.repository;

import com.financialmanajer.financial.category.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByUserIdAndNameIgnoreCaseAndDeletedAtIsNull(Long userId, String name);

    List<CategoryEntity> findAllByUserIdAndDeletedAtIsNullOrderByNameAsc(Long userId);

    Optional<CategoryEntity> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}