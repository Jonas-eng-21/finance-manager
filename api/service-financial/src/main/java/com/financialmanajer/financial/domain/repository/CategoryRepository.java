package com.financialmanajer.financial.domain.repository;

import com.financialmanajer.financial.domain.model.Category;

public interface CategoryRepository {
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
    Category save(Category category);
}