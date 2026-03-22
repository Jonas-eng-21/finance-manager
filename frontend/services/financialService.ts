import { financialApi } from './financialApi';


export type TransactionType = 'INCOME' | 'EXPENSE';

export interface MonthlySummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  month: string;
}

export interface Transaction {
  id: number;
  type: TransactionType;
  amount: number;
  categoryId: number | null;
  description: string | null;
  transactionDate: string;  
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


export const financialService = {
  /**
   * @param month
   */
  async getMonthlySummary(month?: string): Promise<MonthlySummary> {
    const params = month ? { month } : {};
    const { data } = await financialApi.get<MonthlySummary>('/api/transactions/summary', { params });
    return data;
  },


  async getRecentTransactions(size = 5): Promise<PaginatedResult<Transaction, TransactionSummary>> {
    const { data } = await financialApi.get<PaginatedResult<Transaction, TransactionSummary>>(
      '/api/transactions',
      { params: { size, sortBy: 'transactionDate', sortDirection: 'desc' } }
    );
    return data;
  },


  async getActiveGoals(size = 4): Promise<PaginatedResult<Goal>> {
    const { data } = await financialApi.get<PaginatedResult<Goal>>(
      '/api/goals',
      { params: { size, sortBy: 'targetDate', direction: 'asc' } }
    );
    return data;
  },
};
