import { financialApi } from './financialApi';

// ─── Tipos espelhando os DTOs do service-financial ──────────────────────────

export type TransactionType = 'INCOME' | 'EXPENSE';

export interface MonthlySummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  month: string; // "YYYY-MM"
}

export interface Transaction {
  id: number;
  type: TransactionType;
  amount: number;
  categoryId: number | null;
  description: string | null;
  transactionDate: string;   // ISO date "YYYY-MM-DD"
  createdAt: string;
  goalId: number | null;
}

export type GoalStatus = 'IN_PROGRESS' | 'COMPLETED' | 'OVERDUE' | 'ARCHIVED';

export interface Goal {
  id: number;
  name: string;
  targetAmount: number;
  currentAmount: number;
  remainingAmount: number;
  progressPercentage: number;
  status: GoalStatus;
  startDate: string;
  targetDate: string;
  monthlyRequiredSaving: number;
  createdAt: string;
}

export interface PaginatedResult<T, S = null> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  summary: S | null;
}

export interface TransactionSummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
}

// ─── Métodos do serviço ──────────────────────────────────────────────────────

export const financialService = {
  /**
   * Resumo mensal: receitas, despesas e saldo.
   * @param month - "YYYY-MM" (padrão: mês atual)
   */
  async getMonthlySummary(month?: string): Promise<MonthlySummary> {
    const params = month ? { month } : {};
    const { data } = await financialApi.get<MonthlySummary>('/api/transactions/summary', { params });
    return data;
  },

  /**
   * Transações recentes: paginadas, ordenadas por data desc, tamanho padrão 5.
   */
  async getRecentTransactions(size = 5): Promise<PaginatedResult<Transaction, TransactionSummary>> {
    const { data } = await financialApi.get<PaginatedResult<Transaction, TransactionSummary>>(
      '/api/transactions',
      { params: { size, sortBy: 'transactionDate', sortDirection: 'desc' } }
    );
    return data;
  },

  /**
   * Metas ativas: ordenadas por targetDate asc, tamanho padrão 4 para o dashboard.
   */
  async getActiveGoals(size = 4): Promise<PaginatedResult<Goal>> {
    const { data } = await financialApi.get<PaginatedResult<Goal>>(
      '/api/goals',
      { params: { size, sortBy: 'targetDate', direction: 'asc' } }
    );
    return data;
  },
};
