package com.financialmanajer.financial.transaction.presentation.controller;

import com.financialmanajer.financial.shared.domain.exception.DomainValidationException;
import com.financialmanajer.financial.transaction.application.dto.CreateTransactionDTO;
import com.financialmanajer.financial.shared.application.dto.PaginatedResult;
import com.financialmanajer.financial.transaction.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.transaction.application.usecase.*;
import com.financialmanajer.financial.transaction.domain.model.Transaction;
import com.financialmanajer.financial.transaction.presentation.dto.CreateTransactionRequest;
import com.financialmanajer.financial.transaction.presentation.dto.MonthlySummaryResponse;
import com.financialmanajer.financial.transaction.presentation.dto.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.financialmanajer.financial.transaction.application.dto.TransactionSummary;
import com.financialmanajer.financial.transaction.application.dto.TransactionFilterParams;
import com.financialmanajer.financial.transaction.application.dto.UpdateTransactionDTO;
import com.financialmanajer.financial.transaction.presentation.dto.UpdateTransactionRequest;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final GetMonthlySummaryUseCase getMonthlySummaryUseCase;

    public TransactionController(
            CreateTransactionUseCase createTransactionUseCase,
            ListTransactionsUseCase listTransactionsUseCase,
            UpdateTransactionUseCase updateTransactionUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase,
            GetMonthlySummaryUseCase getMonthlySummaryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
        this.getMonthlySummaryUseCase = getMonthlySummaryUseCase;
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

    @GetMapping("/summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(value = "month", required = false) String monthParam) {


        YearMonth month;
        try {
            month = monthParam != null ?
                    YearMonth.parse(monthParam) :
                    YearMonth.now();
        } catch (DateTimeParseException e) {
            throw new DomainValidationException("transaction.validation.month.invalid");
        }

        var summary = getMonthlySummaryUseCase.execute(userId, month);

        return ResponseEntity.ok(
                MonthlySummaryResponse.fromDomain(summary)
        );
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