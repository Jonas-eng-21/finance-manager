package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.UpdateCategoryDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public UpdateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void execute(UpdateCategoryDTO dto) {
        Category category = categoryRepository.findById(dto.id())
                .orElseThrow(() -> new DomainValidationException("category.validation.not_found"));

        if (!category.getUserId().equals(dto.userId())) {
            throw new DomainValidationException("category.validation.access_denied");
        }

        if (!category.getName().equalsIgnoreCase(dto.name().trim())) {
            if (categoryRepository.existsByUserIdAndNameIgnoreCase(dto.userId(), dto.name().trim())) {
                throw new DomainValidationException("category.validation.name.already_exists");
            }
        }

        category.updateName(dto.name());
        categoryRepository.save(category);
    }
}