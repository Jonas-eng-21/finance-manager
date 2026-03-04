package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.dto.CreateTransactionDTO;
import com.financialmanajer.financial.application.usecase.CreateTransactionUseCase;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.presentation.dto.CreateTransactionRequest;
import com.financialmanajer.financial.presentation.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateTransactionRequest request) {

        CreateTransactionDTO dto = new CreateTransactionDTO(
                userId,
                request.type(),
                request.amount(),
                request.categoryId(),
                request.description(),
                request.transactionDate()
        );

        Transaction transaction = createTransactionUseCase.execute(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TransactionResponse.fromDomain(transaction));
    }
}