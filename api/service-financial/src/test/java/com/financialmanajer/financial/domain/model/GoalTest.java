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
        goal.loadCurrentAmount(new BigDecimal("6000.00"));
        assertEquals(new BigDecimal("100.00"), goal.calculateProgressPercentage());
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

    @Test
    @DisplayName("Deve adicionar progresso a uma meta e atualizar o currentAmount")
    void should_add_progress_to_goal() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(10);
        Goal goal = new Goal(1L, "Reserva", new BigDecimal("10000.00"), start, end);

        goal.loadCurrentAmount(new BigDecimal("1000.00"));

        goal.addProgress(new BigDecimal("500.00"));

        assertEquals(new BigDecimal("1500.00"), goal.getCurrentAmount());
        assertNotNull(goal.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve falhar ao tentar adicionar progresso zero ou negativo")
    void should_fail_if_progress_amount_is_invalid() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(10);
        Goal goal = new Goal(1L, "Viagem", new BigDecimal("5000.00"), start, end);

        DomainValidationException ex1 = assertThrows(DomainValidationException.class,
                () -> goal.addProgress(BigDecimal.ZERO));
        assertEquals("goal.validation.progress.positive", ex1.getMessage());

        DomainValidationException ex2 = assertThrows(DomainValidationException.class,
                () -> goal.addProgress(new BigDecimal("-100.00")));
        assertEquals("goal.validation.progress.positive", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve remover progresso da meta garantindo que o valor não fique negativo")
    void should_remove_progress_from_goal() {
        LocalDate start = LocalDate.now();
        Goal goal = new Goal(1L, "Reserva", new BigDecimal("10000.00"), start, start.plusMonths(12));
        goal.loadCurrentAmount(new BigDecimal("1000.00"));

        goal.removeProgress(new BigDecimal("400.00"));
        assertEquals(new BigDecimal("600.00"), goal.getCurrentAmount());

        goal.removeProgress(new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("0.00"), goal.getCurrentAmount());
    }

    @Test
    @DisplayName("Deve retornar true se a meta estiver concluída")
    void should_return_true_if_goal_is_completed() {
        LocalDate start = LocalDate.now();
        Goal goal = new Goal(1L, "Viagem", new BigDecimal("5000.00"), start, start.plusMonths(5));

        assertFalse(goal.isCompleted());

        goal.loadCurrentAmount(new BigDecimal("5000.00"));
        assertTrue(goal.isCompleted());

        goal.loadCurrentAmount(new BigDecimal("6000.00"));
        assertTrue(goal.isCompleted());
    }

    @Test
    @DisplayName("Deve retornar true se a meta estiver próxima do prazo limite")
    void should_return_true_if_goal_is_near_deadline() {
        LocalDate today = LocalDate.of(2025, 5, 25);
        LocalDate deadline = LocalDate.of(2025, 6, 1);

        Goal goal = new Goal(1L, "Japão", new BigDecimal("10000.00"), today.minusMonths(5), deadline);

        assertTrue(goal.isNearDeadline(today, 7));

        assertTrue(goal.isNearDeadline(today, 10));

        assertFalse(goal.isNearDeadline(today, 3));
    }

    @Test
    @DisplayName("Não deve considerar próxima do prazo se a meta já estiver vencida")
    void should_not_be_near_deadline_if_already_overdue() {
        LocalDate today = LocalDate.now();
        LocalDate pastDeadline = today.minusDays(2);

        Goal goal = new Goal(1L, "Dívida", new BigDecimal("1000.00"), today.minusMonths(1), pastDeadline);

        assertFalse(goal.isNearDeadline(today, 7));
    }

    @Test
    @DisplayName("Deve deletar a meta e registrar a data de exclusão")
    void should_delete_goal() {
        Goal goal = new Goal(1L, "Carro", new BigDecimal("50000.00"), LocalDate.now(), LocalDate.now().plusMonths(24));
        assertFalse(goal.isDeleted());
        assertNull(goal.getDeletedAt());

        goal.delete();

        assertTrue(goal.isDeleted());
        assertNotNull(goal.getDeletedAt());

        DomainValidationException exception = assertThrows(DomainValidationException.class, () -> {
            goal.addProgress(new BigDecimal("100.00"));
        });
        assertEquals("goal.already_deleted", exception.getMessage());
    }

    @Test
    @DisplayName("Deve arquivar a meta e registrar a data de arquivamento")
    void should_archive_goal() {
        Goal goal = new Goal(1L, "Reserva", new BigDecimal("5000.00"), LocalDate.now(), LocalDate.now().plusMonths(12));
        assertFalse(goal.isArchived());
        assertNull(goal.getArchivedAt());

        goal.archive();

        assertTrue(goal.isArchived());
        assertNotNull(goal.getArchivedAt());

        DomainValidationException exception = assertThrows(DomainValidationException.class, () -> {
            goal.addProgress(new BigDecimal("100.00"));
        });
        assertEquals("goal.already_archived", exception.getMessage());
    }
}