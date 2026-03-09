package com.financialmanajer.financial.transaction.presentation.controller;

import com.financialmanajer.financial.shared.application.dto.PaginatedResult;
import com.financialmanajer.financial.transaction.application.dto.TransactionFilterDTO;
import com.financialmanajer.financial.transaction.application.usecase.CreateTransactionUseCase;
import com.financialmanajer.financial.transaction.application.usecase.DeleteTransactionUseCase;
import com.financialmanajer.financial.transaction.application.usecase.ListTransactionsUseCase;
import com.financialmanajer.financial.transaction.application.usecase.UpdateTransactionUseCase;
import com.financialmanajer.financial.transaction.domain.model.Transaction;
import com.financialmanajer.financial.transaction.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.financialmanajer.financial.transaction.application.dto.TransactionSummary;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

    @Mock
    private ListTransactionsUseCase listTransactionsUseCase;

    @Mock
    private UpdateTransactionUseCase updateTransactionUseCase;

    @Mock
    private DeleteTransactionUseCase deleteTransactionUseCase;

    @Mock
    private com.financialmanajer.financial.transaction.application.usecase.GetMonthlySummaryUseCase getMonthlySummaryUseCase;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    @DisplayName("Deve retornar 201 Created ao registrar uma transação válida")
    void should_return_201_when_transaction_is_valid() throws Exception {
        Transaction mockTransaction = new Transaction(
                1L, TransactionType.EXPENSE, new BigDecimal("150.75"), 10L, "Supermercado", LocalDate.now()
        );
        mockTransaction.setId(100L);

        when(createTransactionUseCase.execute(any())).thenReturn(mockTransaction);

        String jsonPayload = """
                {
                  "type": "EXPENSE",
                  "amount": 150.75,
                  "categoryId": 10,
                  "description": "Supermercado",
                  "transactionDate": "2026-03-03"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.amount").value(150.75))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request se campos obrigatórios faltarem")
    void should_return_400_when_request_is_invalid() throws Exception {
        String emptyJson = "{}";

        mockMvc.perform(post("/api/transactions")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar transações com paginação e filtros retornando 200 OK")
    void should_list_transactions_with_filters() throws Exception {
        Transaction mockTx = new Transaction(1L, TransactionType.EXPENSE, new BigDecimal("150.00"), 10L, "Teste", LocalDate.now());
        mockTx.setId(100L);

        TransactionSummary mockSummary = new TransactionSummary(BigDecimal.ZERO, new BigDecimal("150.00"), new BigDecimal("-150.00"));
        PaginatedResult<Transaction, TransactionSummary> mockResult = new PaginatedResult<>(List.of(mockTx), 0, 10, 1, 1, mockSummary);

        when(listTransactionsUseCase.execute(any(TransactionFilterDTO.class))).thenReturn(mockResult);
        mockMvc.perform(get("/api/transactions")
                        .header("X-User-Id", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("type", "EXPENSE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.content[0].id").value(100))
                .andExpect(jsonPath("$.content[0].amount").value(150.00));
    }

    @Test
    @DisplayName("Deve atualizar transação com sucesso (PATCH) e retornar 200 OK")
    void should_update_transaction_successfully() throws Exception {
        Transaction mockTx = new Transaction(1L, TransactionType.EXPENSE, new BigDecimal("150.00"), 10L, "Nova Descrição", LocalDate.now());
        mockTx.setId(100L);

        when(updateTransactionUseCase.execute(any())).thenReturn(mockTx);

        mockMvc.perform(patch("/api/transactions/100")
                        .header("X-User-Id", "1")
                        .content("""
                                {
                                  "amount": 150.00,
                                  "description": "Nova Descrição"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.description").value("Nova Descrição"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar atualizar com valor negativo (Validação de Borda)")
    void should_return_400_when_update_amount_is_negative() throws Exception {
        mockMvc.perform(patch("/api/transactions/100")
                        .header("X-User-Id", "1")
                        .content("""
                                {
                                  "amount": -50.00
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(updateTransactionUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve deletar transação com sucesso e retornar 204 No Content")
    void should_delete_transaction_successfully() throws Exception {
        Long transactionId = 100L;
        Long userId = 1L;

        doNothing().when(deleteTransactionUseCase).execute(transactionId, userId);

        mockMvc.perform(delete("/api/transactions/{id}", transactionId)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(deleteTransactionUseCase, times(1)).execute(transactionId, userId);
    }

    @Test
    @DisplayName("Deve retornar 201 Created ao registrar uma transação de INCOME vinculada a uma meta")
    void should_return_201_when_income_transaction_with_goal() throws Exception {
        Transaction mockTransaction = new Transaction(
                1L, TransactionType.INCOME, new BigDecimal("500.00"), 2L, "Bônus", LocalDate.now()
        );
        mockTransaction.setId(100L);
        mockTransaction.linkToGoal(10L);

        when(createTransactionUseCase.execute(any())).thenReturn(mockTransaction);

        String jsonPayload = """
                {
                  "type": "INCOME",
                  "amount": 500.00,
                  "categoryId": 2,
                  "description": "Bônus",
                  "transactionDate": "2026-03-06",
                  "goalId": 10
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.goalId").value(10));
    }

    @org.junit.jupiter.api.Test
    @org.junit.jupiter.api.DisplayName("Deve retornar o resumo financeiro do mes solicitado com status 200 OK")
    void should_return_monthly_summary_with_200_ok() throws Exception {
        Long userId = 1L;
        java.time.YearMonth month = java.time.YearMonth.of(2026, 3);

        com.financialmanajer.financial.transaction.domain.model.MonthlySummary summary =
                new com.financialmanajer.financial.transaction.domain.model.MonthlySummary(
                        new java.math.BigDecimal("3000.00"),
                        new java.math.BigDecimal("1000.00"),
                        null,
                        java.util.Map.of("Alimentação", new java.math.BigDecimal("1000.00"))
                );

        org.mockito.Mockito.when(getMonthlySummaryUseCase.execute(userId, month)).thenReturn(summary);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/transactions/summary")
                        .header("X-User-Id", String.valueOf(userId))
                        .param("month", "2026-03"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalIncomes").value(3000.00))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalExpenses").value(1000.00))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.balance").value(2000.00))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.expensesByCategory.Alimentação").value(1000.00));

        org.mockito.Mockito.verify(getMonthlySummaryUseCase, org.mockito.Mockito.times(1)).execute(userId, month);
    }
}