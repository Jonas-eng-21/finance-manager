package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.financialmanajer.financial.domain.repository.GoalRepository;
import com.financialmanajer.financial.domain.model.Goal;

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