package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.CreateTransactionDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public CreateTransactionUseCase(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Transaction execute(CreateTransactionDTO dto) {
        Category category = categoryRepository.findActiveByIdAndUserId(dto.categoryId(), dto.userId())
                .orElseThrow(() -> new DomainValidationException("category.validation.not_found"));

        if (category.getType() != dto.type()) {
            throw new DomainValidationException("transaction.validation.category_type_mismatch");
        }

        Transaction transaction = new Transaction(
                dto.userId(),
                dto.type(),
                dto.amount(),
                dto.categoryId(),
                dto.description(),
                dto.transactionDate()
        );

        return transactionRepository.save(transaction);
    }
}