package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.dto.CreateTransactionDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.application.usecase.CreateTransactionUseCase;
import com.financialmanajer.financial.application.usecase.DeleteTransactionUseCase;
import com.financialmanajer.financial.application.usecase.ListTransactionsUseCase;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.model.TransactionType;
import com.financialmanajer.financial.presentation.dto.CreateTransactionRequest;
import com.financialmanajer.financial.presentation.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.financialmanajer.financial.application.dto.TransactionSummary;
import java.math.BigDecimal;
import com.financialmanajer.financial.presentation.dto.TransactionFilterParams;
import com.financialmanajer.financial.application.dto.UpdateTransactionDTO;
import com.financialmanajer.financial.application.usecase.UpdateTransactionUseCase;
import com.financialmanajer.financial.presentation.dto.UpdateTransactionRequest;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    public TransactionController(
            CreateTransactionUseCase createTransactionUseCase,
            ListTransactionsUseCase listTransactionsUseCase,
            UpdateTransactionUseCase updateTransactionUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
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
                request.transactionDate(),
                request.goalId()
        );

        Transaction transaction = createTransactionUseCase.execute(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TransactionResponse.fromDomain(transaction));
    }

    @GetMapping
    public ResponseEntity<PaginatedResult<TransactionResponse, TransactionSummary>> listTransactions(
            @RequestHeader("X-User-Id") Long userId,
            TransactionFilterParams params
    ) {

        TransactionFilterDTO filter = new TransactionFilterDTO(
                userId, params.startDate(), params.endDate(), params.type(),
                params.categoryId(), params.minAmount(), params.maxAmount(),
                params.sortBy(), params.sortDirection(),
                params.getPageOrDefault(), params.getSizeOrDefault()
        );

        PaginatedResult<Transaction, TransactionSummary> domainResult = listTransactionsUseCase.execute(filter);

        List<TransactionResponse> responseContent = domainResult.content().stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        PaginatedResult<TransactionResponse, TransactionSummary> response = new PaginatedResult<>(
                responseContent, domainResult.page(), domainResult.size(),
                domainResult.totalElements(), domainResult.totalPages(), domainResult.summary()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateTransactionRequest request) {

        UpdateTransactionDTO dto = new UpdateTransactionDTO(
                id,
                userId,
                request.type(),
                request.amount(),
                request.categoryId(),
                request.description(),
                request.transactionDate()
        );

        Transaction transaction = updateTransactionUseCase.execute(dto);

        return ResponseEntity.ok(TransactionResponse.fromDomain(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {

        deleteTransactionUseCase.execute(id, userId);

        return ResponseEntity.noContent().build();
    }
}