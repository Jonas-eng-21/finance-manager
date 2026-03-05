package com.financialmanajer.financial.domain.model;

import com.financialmanajer.financial.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Goal {

    private Long id;
    private final Long userId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate startDate;
    private LocalDate targetDate;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Goal(Long userId, String name, BigDecimal targetAmount, LocalDate startDate, LocalDate targetDate) {
        validate(name, targetAmount, startDate, targetDate);

        this.userId = userId;
        this.name = name.trim();
        this.targetAmount = targetAmount.setScale(2, RoundingMode.HALF_UP);
        this.currentAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.startDate = startDate;
        this.targetDate = targetDate;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    private void validate(String name, BigDecimal targetAmount, LocalDate startDate, LocalDate targetDate) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidationException("goal.validation.name.required");
        }
        if (name.trim().length() < 3 || name.trim().length() > 100) {
            throw new DomainValidationException("goal.validation.name.size");
        }
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("goal.validation.target_amount.positive");
        }
        if (startDate == null || targetDate == null) {
            throw new DomainValidationException("goal.validation.date.required");
        }
        if (!targetDate.isAfter(startDate)) {
            throw new DomainValidationException("goal.validation.target_date.after_start_date");
        }
    }

    public BigDecimal calculateMonthlyRequiredSaving(LocalDate currentDate) {
        BigDecimal remainingAmount = targetAmount.subtract(currentAmount).max(BigDecimal.ZERO);

        if (currentDate.isAfter(targetDate) || currentDate.isEqual(targetDate)) {
            return remainingAmount;
        }

        long monthsRemaining = ChronoUnit.MONTHS.between(currentDate, targetDate);

        if (monthsRemaining <= 0) {
            monthsRemaining = 1;
        }

        return remainingAmount.divide(BigDecimal.valueOf(monthsRemaining), 2, RoundingMode.HALF_UP);
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getTargetDate() { return targetDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    public void loadCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount != null ? currentAmount : BigDecimal.ZERO;
    }
    public void loadUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void loadDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}