package com.financialmanajer.financial.transaction.infrastructure.persistence.repository;

import com.financialmanajer.financial.category.domain.model.Category;
import com.financialmanajer.financial.category.domain.repository.CategoryRepository;
import com.financialmanajer.financial.transaction.domain.model.MonthlySummary;
import com.financialmanajer.financial.transaction.domain.repository.MonthlySummaryRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MonthlySummaryRepositoryImpl implements MonthlySummaryRepository {

    private final SpringDataTransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public MonthlySummaryRepositoryImpl(SpringDataTransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public MonthlySummary getSummary(Long userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        BigDecimal incomes = transactionRepository.sumIncomes(userId, startDate, endDate);
        BigDecimal expenses = transactionRepository.sumExpenses(userId, startDate, endDate);

        if (incomes == null) incomes = BigDecimal.ZERO;
        if (expenses == null) expenses = BigDecimal.ZERO;

        List<SpringDataTransactionRepository.CategoryExpenseProjection> projections =
                transactionRepository.getExpensesByCategoryId(userId, startDate, endDate);

        Map<String, BigDecimal> expensesByCategory = new HashMap<>();

        for (SpringDataTransactionRepository.CategoryExpenseProjection proj : projections) {
            String categoryName = categoryRepository.findActiveByIdAndUserId(proj.getCategoryId(), userId)
                    .map(Category::getName)
                    .orElse("category.unknown");

            expensesByCategory.put(categoryName, proj.getTotalAmount());
        }

        return new MonthlySummary(incomes, expenses, null, expensesByCategory);
    }
}