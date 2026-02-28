package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.dto.CreateCategoryDTO;
import com.financialmanajer.financial.application.usecase.CreateCategoryUseCase;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.presentation.dto.CategoryResponse;
import com.financialmanajer.financial.presentation.dto.CreateCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCategoryRequest request) {

        CreateCategoryDTO dto = new CreateCategoryDTO(userId, request.name());

        Category category = createCategoryUseCase.execute(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CategoryResponse.fromDomain(category));
    }
}