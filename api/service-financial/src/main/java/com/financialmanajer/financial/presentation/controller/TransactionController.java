package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.dto.CreateTransactionDTO;
import com.financialmanajer.financial.application.dto.PaginatedResult;
import com.financialmanajer.financial.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.application.usecase.CreateTransactionUseCase;
import com.financialmanajer.financial.application.usecase.ListTransactionsUseCase;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.model.TransactionType;
import com.financialmanajer.financial.presentation.dto.CreateTransactionRequest;
import com.financialmanajer.financial.presentation.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;

    public TransactionController(
            CreateTransactionUseCase createTransactionUseCase,
            ListTransactionsUseCase listTransactionsUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
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

    @GetMapping
    public ResponseEntity<PaginatedResult<TransactionResponse>> listTransactions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        TransactionFilterDTO filter = new TransactionFilterDTO(
                userId, startDate, endDate, type, categoryId, page, size
        );

        PaginatedResult<Transaction> domainResult = listTransactionsUseCase.execute(filter);

        List<TransactionResponse> responseContent = domainResult.content().stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        PaginatedResult<TransactionResponse> response = new PaginatedResult<>(
                responseContent,
                domainResult.page(),
                domainResult.size(),
                domainResult.totalElements(),
                domainResult.totalPages()
        );

        return ResponseEntity.ok(response);
    }
}