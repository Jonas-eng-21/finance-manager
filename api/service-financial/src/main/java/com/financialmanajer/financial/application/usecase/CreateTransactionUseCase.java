package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.application.dto.CreateTransactionDTO;
import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.exception.ResourceNotFoundException;
import com.financialmanajer.financial.domain.model.Category;
import com.financialmanajer.financial.domain.model.Goal;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.CategoryRepository;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;

    public CreateTransactionUseCase(TransactionRepository transactionRepository, CategoryRepository categoryRepository, GoalRepository goalRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.goalRepository = goalRepository;
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

        if (dto.goalId() != null) {
            Goal goal = goalRepository.findByIdAndUserId(dto.goalId(), dto.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("goal.not_found"));

            transaction.linkToGoal(goal.getId());

            goal.addProgress(dto.amount());

            goalRepository.save(goal);
        }

        return transactionRepository.save(transaction);
    }
}