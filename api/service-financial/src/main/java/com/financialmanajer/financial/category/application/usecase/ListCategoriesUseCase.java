package com.financialmanajer.financial.category.application.usecase;

import com.financialmanajer.financial.category.domain.model.Category;
import com.financialmanajer.financial.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public ListCategoriesUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> execute(Long userId) {
        return categoryRepository.findAllByUserId(userId);
    }
}