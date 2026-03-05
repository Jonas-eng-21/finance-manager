package com.financialmanajer.financial.application.usecase;

import com.financialmanajer.financial.domain.exception.DomainValidationException;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public DeleteTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void execute(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findActiveByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new DomainValidationException("transaction.validation.not_found"));

        transaction.delete();

        transactionRepository.save(transaction);
    }
}