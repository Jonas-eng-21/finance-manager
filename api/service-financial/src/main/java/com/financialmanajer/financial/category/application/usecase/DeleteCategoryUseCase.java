package com.financialmanajer.financial.category.application.usecase;

import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.category.domain.model.Category;
import com.financialmanajer.financial.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void execute(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainValidationException("category.validation.not_found"));

        if (!category.getUserId().equals(userId)) {
            throw new DomainValidationException("category.validation.access_denied");
        }

        if (category.isDeleted()) {
            return;
        }

        category.delete();
        categoryRepository.save(category);
    }
}