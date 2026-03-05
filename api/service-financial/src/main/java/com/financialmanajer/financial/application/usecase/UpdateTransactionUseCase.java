package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.UpdateTransactionDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public UpdateTransactionUseCase(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Transaction execute(UpdateTransactionDTO dto) {
        Transaction transaction = transactionRepository.findActiveByIdAndUserId(dto.id(), dto.userId())
                .orElseThrow(() -> new DomainValidationException("transaction.validation.not_found"));

        if (dto.categoryId() != null) {
            Category newCategory = categoryRepository.findActiveByIdAndUserId(dto.categoryId(), dto.userId())
                    .orElseThrow(() -> new DomainValidationException("category.validation.not_found"));

            var targetType = dto.type() != null ? dto.type() : transaction.getType();

            if (newCategory.getType() != targetType) {
                throw new DomainValidationException("transaction.validation.category_type_mismatch");
            }
        } else if (dto.type() != null) {
            Category currentCategory = categoryRepository.findActiveByIdAndUserId(transaction.getCategoryId(), dto.userId())
                    .orElseThrow(() -> new DomainValidationException("category.validation.not_found"));

            if (currentCategory.getType() != dto.type()) {
                throw new DomainValidationException("transaction.validation.category_type_mismatch");
            }
        }

        transaction.update(
                dto.type(),
                dto.amount(),
                dto.categoryId(),
                dto.description(),
                dto.transactionDate()
        );

        return transactionRepository.save(transaction);
    }
}