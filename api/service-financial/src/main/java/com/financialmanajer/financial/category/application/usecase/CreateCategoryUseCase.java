package com.financialmanajer.financial.category.application.usecase;

import com.financialmanajer.financial.category.application.dto.CreateCategoryDTO;
import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.category.domain.model.Category;
import com.financialmanajer.financial.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CreateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category execute(CreateCategoryDTO dto) {
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(dto.userId(), dto.name())) {
            throw new DomainValidationException("category.validation.name.already_exists");
        }

        Category category = new Category(dto.userId(), dto.name(), dto.type());

        return categoryRepository.save(category);
    }
}