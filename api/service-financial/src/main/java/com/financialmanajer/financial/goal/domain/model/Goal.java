package com.financialmanajer.financial.goal.domain.model;

import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;

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
    private LocalDateTime archivedAt;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public boolean isCompleted() {
        return this.currentAmount.compareTo(this.targetAmount) >= 0;
    }

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

    public BigDecimal calculateRemainingAmount() {
        return targetAmount.subtract(currentAmount)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal percentage = currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        return percentage.min(new BigDecimal("100.00"));
    }

    public GoalStatus getStatus(LocalDate currentDate) {
        if (currentAmount.compareTo(targetAmount) >= 0) {
            return GoalStatus.COMPLETED;
        }
        if (currentDate.isAfter(targetDate)) {
            return GoalStatus.OVERDUE;
        }
        return GoalStatus.IN_PROGRESS;
    }

    public void addProgress(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("goal.validation.progress.positive");
        }

        if (this.isDeleted()) {
            throw new DomainValidationException("goal.already_deleted");
        }

        if (this.isArchived()) {
            throw new DomainValidationException("goal.already_archived");
        }

        this.currentAmount = this.currentAmount.add(amount).setScale(2, RoundingMode.HALF_UP);

        this.updatedAt = LocalDateTime.now();
    }

    public void removeProgress(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("goal.validation.progress.positive");
        }

        this.currentAmount = this.currentAmount.subtract(amount);

        if (this.currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.currentAmount = BigDecimal.ZERO;
        }

        if (this.isDeleted()) {
            throw new DomainValidationException("goal.already_deleted");
        }

        if (this.isArchived()) {
            throw new DomainValidationException("goal.already_archived");
        }

        this.currentAmount = this.currentAmount.setScale(2, java.math.RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isNearDeadline(LocalDate today, int thresholdDays) {
        if (today.isAfter(this.targetDate)) {
            return false;
        }

        long daysRemaining = ChronoUnit.DAYS.between(today, this.targetDate);

        return daysRemaining <= thresholdDays;
    }

    public void delete() {
        if (this.isDeleted()) {
            throw new DomainValidationException("goal.already_deleted");
        }
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void loadArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public boolean isArchived() {
        return this.archivedAt != null;
    }

    public void archive() {
        if (this.isArchived()) {
            throw new DomainValidationException("goal.already_archived");
        }
        this.archivedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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