package com.financialmanajer.financial.domain.model;

import com.financialmanajer.financial.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private final Long userId;
    private TransactionType type;
    private BigDecimal amount;
    private Long categoryId;
    private String description;
    private LocalDate transactionDate;
    private final LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public Transaction(Long userId, TransactionType type, BigDecimal amount, Long categoryId, String description, LocalDate transactionDate) {
        validate(userId, type, amount, categoryId, description, transactionDate);

        this.userId = userId;
        this.type = type;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.categoryId = categoryId;
        this.description = description == null ? "" : description.trim();
        this.transactionDate = transactionDate;
        this.createdAt = LocalDateTime.now();
    }

    private void validate(Long userId, TransactionType type, BigDecimal amount, Long categoryId, String description, LocalDate transactionDate) {
        if (userId == null) throw new DomainValidationException("transaction.validation.user_id.required");
        if (type == null) throw new DomainValidationException("transaction.validation.type.required");
        if (categoryId == null) throw new DomainValidationException("transaction.validation.category_id.required");
        if (transactionDate == null) throw new DomainValidationException("transaction.validation.date.required");

        if (amount == null) {
            throw new DomainValidationException("transaction.validation.amount.required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("transaction.validation.amount.must_be_positive");
        }

        if (description != null && description.trim().length() > 255) {
            throw new DomainValidationException("transaction.validation.description.too_long");
        }
    }

    public void update(TransactionType type, BigDecimal amount, Long categoryId, String description, LocalDate transactionDate) {
        if (amount != null) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new DomainValidationException("transaction.validation.amount.positive");
            }
            this.amount = amount.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        if (type != null) this.type = type;
        if (categoryId != null) this.categoryId = categoryId;
        if (description != null) this.description = description;
        if (transactionDate != null) this.transactionDate = transactionDate;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void loadDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Long getCategoryId() { return categoryId; }
    public String getDescription() { return description; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}