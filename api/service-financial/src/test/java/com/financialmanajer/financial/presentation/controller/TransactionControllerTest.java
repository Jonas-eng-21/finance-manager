package com.financialmanajer.financial.presentation.controller;

import com.financialmanajer.financial.application.usecase.CreateTransactionUseCase;
import com.financialmanajer.financial.domain.model.Transaction;
import com.financialmanajer.financial.domain.model.TransactionType;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

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
}