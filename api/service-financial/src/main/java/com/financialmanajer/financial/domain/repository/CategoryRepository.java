package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.domain.model.Category;

import java.util.List;

public interface CategoryRepository {
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
    Category save(Category category);
    List<Category> findAllByUserId(Long userId);
}