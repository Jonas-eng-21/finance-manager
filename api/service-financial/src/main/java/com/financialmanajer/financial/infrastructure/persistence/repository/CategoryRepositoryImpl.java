package com.financialmanajer.financial.infrastructure.persistence.repository;

import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import com.financialmanajer.financial.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final SpringDataCategoryRepository springDataRepository;

    public CategoryRepositoryImpl(SpringDataCategoryRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public boolean existsByUserIdAndNameIgnoreCase(Long userId, String name) {
        return springDataRepository.existsByUserIdAndNameIgnoreCase(userId, name);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setUserId(category.getUserId());
        entity.setName(category.getName());
        entity.setCreatedAt(category.getCreatedAt());

        CategoryEntity savedEntity = springDataRepository.save(entity);

        category.setId(savedEntity.getId());
        return category;
    }
}