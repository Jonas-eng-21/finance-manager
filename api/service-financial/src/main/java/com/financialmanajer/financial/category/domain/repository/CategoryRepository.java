package com.financialmanajer.financial.category.domain.repository;

import com.financialmanajer.financial.category.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
    Category save(Category category);
    List<Category> findAllByUserId(Long userId);
    Optional<Category> findById(Long id);
    Optional<Category> findActiveByIdAndUserId(Long id, Long userId);
}