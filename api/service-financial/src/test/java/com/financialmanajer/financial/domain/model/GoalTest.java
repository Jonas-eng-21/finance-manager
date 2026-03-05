package com.financialmanajer.financial.domain.model;

import com.financialmanajer.financial.domain.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GoalTest {

    @Test
    @DisplayName("Deve criar uma meta válida com currentAmount inicializado em zero")
    void should_create_valid_goal() {
        Goal goal = new Goal(
                1L,
                "Reserva de Emergência",
                new BigDecimal("10000.00"),
                LocalDate.now(),
                LocalDate.now().plusMonths(10)
        );

        assertNotNull(goal.getCreatedAt());
        assertEquals("Reserva de Emergência", goal.getName());
        assertEquals(new BigDecimal("10000.00"), goal.getTargetAmount());
        assertEquals(new BigDecimal("0.00"), goal.getCurrentAmount());
        assertFalse(goal.isDeleted());
    }

    @Test
    @DisplayName("Deve falhar se o valor alvo (targetAmount) for zero ou negativo")
    void should_fail_if_target_amount_is_invalid() {
        DomainValidationException ex1 = assertThrows(DomainValidationException.class, () ->
                new Goal(1L, "Carro", BigDecimal.ZERO, LocalDate.now(), LocalDate.now().plusMonths(10))
        );
        assertEquals("goal.validation.target_amount.positive", ex1.getMessage());

        DomainValidationException ex2 = assertThrows(DomainValidationException.class, () ->
                new Goal(1L, "Carro", new BigDecimal("-100"), LocalDate.now(), LocalDate.now().plusMonths(10))
        );
        assertEquals("goal.validation.target_amount.positive", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se a data final for anterior ou igual à data inicial")
    void should_fail_if_target_date_is_invalid() {
        LocalDate today = LocalDate.now();

        DomainValidationException ex = assertThrows(DomainValidationException.class, () ->
                new Goal(1L, "Viagem", new BigDecimal("5000"), today, today.minusDays(1))
        );
        assertEquals("goal.validation.target_date.after_start_date", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se o nome da meta for nulo, vazio ou fora do limite de caracteres")
    void should_fail_if_name_is_invalid() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(1);
        BigDecimal amount = new BigDecimal("1000");

        DomainValidationException ex1 = assertThrows(DomainValidationException.class, () ->
                new Goal(1L, null, amount, start, end)
        );
        assertEquals("goal.validation.name.required", ex1.getMessage());

        DomainValidationException ex2 = assertThrows(DomainValidationException.class, () ->
                new Goal(1L, "A", amount, start, end)
        );
        assertEquals("goal.validation.name.size", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve calcular corretamente a economia mensal necessária (monthlyRequiredSaving)")
    void should_calculate_monthly_required_saving() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(10);

        Goal goal = new Goal(1L, "Reserva", new BigDecimal("10000"), start, end);

        assertEquals(new BigDecimal("1000.00"), goal.calculateMonthlyRequiredSaving(start));
    }

    @Test
    @DisplayName("Deve calcular corretamente o valor restante (remainingAmount)")
    void should_calculate_remaining_amount() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(10);

        Goal goal = new Goal(1L, "Carro", new BigDecimal("50000.00"), start, end);

        assertEquals(new BigDecimal("50000.00"), goal.calculateRemainingAmount());

        goal.loadCurrentAmount(new BigDecimal("15000.00"));
        assertEquals(new BigDecimal("35000.00"), goal.calculateRemainingAmount());

        goal.loadCurrentAmount(new BigDecimal("55000.00"));
        assertEquals(new BigDecimal("0.00"), goal.calculateRemainingAmount());
    }

    @Test
    @DisplayName("Deve calcular corretamente o percentual de progresso limitando a 100%")
    void should_calculate_progress_percentage() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(10);
        Goal goal = new Goal(1L, "Reserva", new BigDecimal("10000.00"), start, end);

        goal.loadCurrentAmount(new BigDecimal("5000.00"));
        assertEquals(new BigDecimal("50.00"), goal.calculateProgressPercentage());

        goal = new Goal(1L, "Viagem", new BigDecimal("5000.00"), start, end);
        goal.loadCurrentAmount(new BigDecimal("6000.00")); // Passou da meta!
        assertEquals(new BigDecimal("100.00"), goal.calculateProgressPercentage()); // 👈 Agora crava em 100
    }

    @Test
    @DisplayName("Deve determinar o status correto da meta")
    void should_determine_correct_status() {
        LocalDate today = LocalDate.now();
        Goal goal = new Goal(1L, "Carro", new BigDecimal("50000.00"), today, today.plusMonths(5));

        goal.loadCurrentAmount(new BigDecimal("10000.00"));
        assertEquals(GoalStatus.IN_PROGRESS, goal.getStatus(today));

        goal.loadCurrentAmount(new BigDecimal("50000.00"));
        assertEquals(GoalStatus.COMPLETED, goal.getStatus(today));

        Goal overdueGoal = new Goal(1L, "Dívida", new BigDecimal("1000.00"), today.minusMonths(2), today.minusDays(1));
        overdueGoal.loadCurrentAmount(new BigDecimal("500.00"));
        assertEquals(GoalStatus.OVERDUE, overdueGoal.getStatus(today));
    }
}