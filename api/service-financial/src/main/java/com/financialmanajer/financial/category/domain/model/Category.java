package com.financialmanajer.financial.category.domain.model;

import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.transaction.domain.model.TransactionType;

import java.time.LocalDateTime;

public class Category {
    private Long id;
    private Long userId;
    private String name;
    private LocalDateTime createdAt;
    private java.time.LocalDateTime deletedAt;
    private TransactionType type;

    public Category(Long userId, String name, TransactionType type) {
        validateName(name);
        if (userId == null) throw new DomainValidationException("category.validation.user_id.required");
        if (type == null) throw new DomainValidationException("transaction.validation.type.required");

        this.userId = userId;
        this.name = name.trim();
        this.type = type;
        this.createdAt = java.time.LocalDateTime.now();
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

    public void updateName(String newName) {
        if (newName == null) {
            throw new DomainValidationException("category.validation.name.required");
        }

        String normalizedName = newName.trim();

        validateName(normalizedName);
        this.name = normalizedName;
    }

    public void delete() {
        this.deletedAt = java.time.LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public java.time.LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void loadDeletedAt(java.time.LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TransactionType getType() { return type; }

    public void setId(Long id) {
        this.id = id;
    }
}