package com.financialmanajer.financial.domain.model;

import com.financialmanajer.financial.domain.exception.DomainValidationException;
import java.time.LocalDateTime;

public class Category {
    private Long id;
    private Long userId;
    private String name;
    private LocalDateTime createdAt;

    public Category(Long userId, String name) {
        validateUserId(userId);
        validateName(name);

        this.userId = userId;
        this.name = name.trim();
        this.createdAt = LocalDateTime.now();
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new DomainValidationException("category.validation.user_id.required");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidationException("category.validation.name.required");
        }
        if (name.trim().length() < 2) {
            throw new DomainValidationException("category.validation.name.min_length");
        }
        if (name.trim().length() > 50) {
            throw new DomainValidationException("category.validation.name.max_length");
        }
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) {
        this.id = id;
    }
}