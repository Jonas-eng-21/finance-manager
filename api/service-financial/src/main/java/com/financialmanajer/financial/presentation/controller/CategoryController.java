package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.dto.CreateCategoryDTO;
import com.financialmanajer.financial.application.dto.UpdateCategoryDTO;
import com.financialmanajer.financial.application.usecase.CreateCategoryUseCase;
import com.financialmanajer.financial.application.usecase.DeleteCategoryUseCase;
import com.financialmanajer.financial.application.usecase.ListCategoriesUseCase;
import com.financialmanajer.financial.application.usecase.UpdateCategoryUseCase;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.presentation.dto.CategoryResponse;
import com.financialmanajer.financial.presentation.dto.CreateCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(
            CreateCategoryUseCase createCategoryUseCase,
            ListCategoriesUseCase listCategoriesUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCategoryRequest request) {

        CreateCategoryDTO dto = new CreateCategoryDTO(userId, request.name(), request.type());

        Category category = createCategoryUseCase.execute(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CategoryResponse.fromDomain(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories(@RequestHeader("X-User-Id") Long userId) {
        var categories = listCategoriesUseCase.execute(userId);
        var response = categories.stream()
                .map(CategoryResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCategoryRequest request) {

        updateCategoryUseCase.execute(new UpdateCategoryDTO(id, userId, request.name()));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        deleteCategoryUseCase.execute(id, userId);

        return ResponseEntity.noContent().build();
    }
}