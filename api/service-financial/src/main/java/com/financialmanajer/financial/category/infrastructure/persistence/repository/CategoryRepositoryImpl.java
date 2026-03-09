package com.financialmanajer.financial.category.infrastructure.persistence.repository;

import com.financialmanajer.financial.category.domain.model.Category;
import com.financialmanajer.financial.category.domain.repository.CategoryRepository;
import com.financialmanajer.financial.category.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final SpringDataCategoryRepository springDataRepository;

    public CategoryRepositoryImpl(SpringDataCategoryRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public boolean existsByUserIdAndNameIgnoreCase(Long userId, String name) {
        return springDataRepository.existsByUserIdAndNameIgnoreCaseAndDeletedAtIsNull(userId, name);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = new CategoryEntity();

        if (category.getId() != null) {
            entity = springDataRepository.findById(category.getId()).orElse(new CategoryEntity());
        }

        entity.setUserId(category.getUserId());
        entity.setName(category.getName());

        entity.setType(category.getType());

        entity.setCreatedAt(category.getCreatedAt());
        entity.setDeletedAt(category.getDeletedAt());

        CategoryEntity savedEntity = springDataRepository.save(entity);

        category.setId(savedEntity.getId());
        return category;
    }

    @Override
    public List<Category> findAllByUserId(Long userId) {
        return springDataRepository.findAllByUserIdAndDeletedAtIsNullOrderByNameAsc(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    private Category toDomain(CategoryEntity entity) {
        Category domain = new Category(entity.getUserId(), entity.getName(), entity.getType());
        domain.setId(entity.getId());
        if (entity.getDeletedAt() != null) {
            domain.loadDeletedAt(entity.getDeletedAt());
        }
        return domain;
    }

    @Override
    public Optional<Category> findActiveByIdAndUserId(Long id, Long userId) {
        return springDataRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .map(this::toDomain);
    }
}