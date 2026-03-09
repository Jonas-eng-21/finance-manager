package com.financialmanajer.financial.transaction.application.usecase;

import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.transaction.domain.model.Transaction;
import com.financialmanajer.financial.transaction.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.financialmanajer.financial.goal.domain.repository.GoalRepository;

@Service
public class DeleteTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final GoalRepository goalRepository;

    public DeleteTransactionUseCase(TransactionRepository transactionRepository, GoalRepository goalRepository) {
        this.transactionRepository = transactionRepository;
        this.goalRepository = goalRepository;
    }

    public void execute(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findActiveByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new DomainValidationException("transaction.validation.not_found"));

        if (transaction.getGoalId() != null) {
            goalRepository.findByIdAndUserId(transaction.getGoalId(), userId).ifPresent(goal -> {
                goal.removeProgress(transaction.getAmount());
                goalRepository.save(goal);
            });
        }

        transaction.delete();

        transactionRepository.save(transaction);
    }
}