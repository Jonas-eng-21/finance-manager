"use client";

import { useEffect, useState, useCallback } from "react";
import { useTranslations } from "next-intl";
import { useAuth } from "@/hooks/useAuth";
import { useRouter } from "@/i18n/routing";
import {
  financialService,
  type MonthlySummary,
  type Goal,
  type Transaction,
} from "@/services/financialService";
import {
  Wallet,
  TrendingUp,
  TrendingDown,
  ArrowUpRight,
  ArrowDownRight,
  Target,
  RefreshCw,
} from "lucide-react";
import Cookies from "js-cookie";
import { PublicLayout } from "@/components/layout/PublicLayout";

function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
    minimumFractionDigits: 2,
  }).format(value);
}

function formatDate(dateStr: string) {
  return new Intl.DateTimeFormat("pt-BR", { day: "2-digit", month: "short" }).format(
    new Date(dateStr + "T00:00:00")
  );
}

function Skeleton({ className = "" }: { className?: string }) {
  return <div className={`animate-pulse rounded-lg bg-muted ${className}`} aria-hidden="true" />;
}

export default function DashboardPage() {
  const t = useTranslations("Dashboard");
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();

  const [summary, setSummary] = useState<MonthlySummary | null>(null);
  const [goals, setGoals] = useState<Goal[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const fetchData = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    setError(false);

    if (!Cookies.get("user_id")) {
      Cookies.set("user_id", String(user.id), { expires: 7, secure: true, sameSite: "lax" });
    }

    try {
      const [summaryData, goalsData, txData] = await Promise.all([
        financialService.getMonthlySummary(),
        financialService.getActiveGoals(4),
        financialService.getRecentTransactions(5),
      ]);
      setSummary(summaryData);
      setGoals(goalsData.content);
      setTransactions(txData.content);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    if (!authLoading && !user) router.push("/login");
    if (!authLoading && user) fetchData();
  }, [authLoading, user, fetchData, router]);

  if (authLoading) {
    return (
      <PublicLayout>
        <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center">
          <div className="flex flex-col items-center gap-3 text-muted-foreground">
            <div className="h-10 w-10 rounded-xl bg-primary/10 flex items-center justify-center">
              <Wallet size={20} className="text-primary animate-pulse" />
            </div>
            <p className="text-sm">{t("loading")}</p>
          </div>
        </div>
      </PublicLayout>
    );
  }

  return (
    <PublicLayout>
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-6 space-y-6">

        {/* Error state */}
        {error && !loading && (
          <div className="rounded-xl border border-destructive/20 bg-destructive/5 p-4 flex items-center justify-between gap-4">
            <p className="text-sm text-destructive font-medium">{t("error")}</p>
            <button
              onClick={fetchData}
              className="flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-medium bg-destructive/10 text-destructive hover:bg-destructive/20 transition-colors"
            >
              <RefreshCw size={12} />
              {t("retry")}
            </button>
          </div>
        )}

        {/* Balance Hero Card */}
        <div className="relative overflow-hidden rounded-2xl bg-primary p-6 sm:p-8 shadow-lg">
          <div className="absolute -top-12 -right-12 h-48 w-48 rounded-full bg-white/5" />
          <div className="absolute -bottom-8 -right-4 h-32 w-32 rounded-full bg-white/5" />
          <div className="relative z-10">
            <p className="text-sm font-medium text-primary-foreground/70 uppercase tracking-wider mb-1">
              {t("balance.title")}
            </p>
            {loading ? (
              <div className="space-y-2 mt-2">
                <Skeleton className="h-12 w-56 bg-white/10" />
                <Skeleton className="h-4 w-32 bg-white/10" />
              </div>
            ) : (
              <>
                <p className="text-4xl sm:text-5xl font-bold text-primary-foreground tracking-tight mt-2">
                  {formatCurrency(summary?.balance ?? 0)}
                </p>
                <div className="flex items-center gap-2 mt-3">
                  {(summary?.balance ?? 0) >= 0 ? (
                    <TrendingUp size={16} className="text-primary-foreground/70" />
                  ) : (
                    <TrendingDown size={16} className="text-primary-foreground/70" />
                  )}
                  <span className="text-sm text-primary-foreground/70">
                    {t("balance.month")} •{" "}
                    {(summary?.balance ?? 0) >= 0 ? t("balance.trend_up") : t("balance.trend_down")}
                  </span>
                </div>
              </>
            )}
          </div>
        </div>

        {/* KPI Cards */}
        <div className="grid grid-cols-2 gap-4">
          <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide">
                {t("kpi.income")}
              </p>
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-income/10">
                <ArrowUpRight size={16} className="text-income" />
              </div>
            </div>
            {loading ? (
              <Skeleton className="h-7 w-28" />
            ) : (
              <p className="text-xl sm:text-2xl font-bold text-income">
                {formatCurrency(summary?.totalIncome ?? 0)}
              </p>
            )}
            <p className="text-xs text-muted-foreground mt-1">{t("kpi.this_month")}</p>
          </div>

          <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide">
                {t("kpi.expense")}
              </p>
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-expense/10">
                <ArrowDownRight size={16} className="text-expense" />
              </div>
            </div>
            {loading ? (
              <Skeleton className="h-7 w-28" />
            ) : (
              <p className="text-xl sm:text-2xl font-bold text-expense">
                {formatCurrency(summary?.totalExpense ?? 0)}
              </p>
            )}
            <p className="text-xs text-muted-foreground mt-1">{t("kpi.this_month")}</p>
          </div>
        </div>

        {/* Goals + Transactions grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

          {/* Goals */}
          <section className="rounded-xl border border-border bg-card shadow-sm">
            <div className="flex items-center justify-between px-6 py-4 border-b border-border">
              <h2 className="text-sm font-semibold text-foreground">{t("goals.title")}</h2>
              <Target size={16} className="text-primary" />
            </div>
            <div className="divide-y divide-border">
              {loading ? (
                Array.from({ length: 3 }).map((_, i) => (
                  <div key={i} className="px-6 py-4 space-y-2">
                    <Skeleton className="h-4 w-36" />
                    <Skeleton className="h-2 w-full" />
                    <Skeleton className="h-3 w-24" />
                  </div>
                ))
              ) : goals.length === 0 ? (
                <p className="px-6 py-8 text-center text-sm text-muted-foreground">{t("goals.empty")}</p>
              ) : (
                goals.map((goal) => {
                  const pct = Math.min(Math.round(goal.progressPercentage), 100);
                  const isOverdue = goal.status === "OVERDUE";
                  const isCompleted = goal.status === "COMPLETED";
                  return (
                    <div key={goal.id} className="px-6 py-4">
                      <div className="flex items-center justify-between mb-2">
                        <p className="text-sm font-medium text-foreground truncate pr-4">{goal.name}</p>
                        <span className={`text-xs font-medium px-2 py-0.5 rounded-full shrink-0 ${
                          isCompleted
                            ? "bg-success/10 text-success"
                            : isOverdue
                            ? "bg-expense/10 text-expense"
                            : "bg-secondary/20 text-secondary-foreground"
                        }`}>
                          {isCompleted
                            ? t("goals.status_completed")
                            : isOverdue
                            ? t("goals.status_overdue")
                            : t("goals.status_in_progress")}
                        </span>
                      </div>
                      <div className="h-1.5 w-full rounded-full bg-muted overflow-hidden">
                        <div
                          className={`h-full rounded-full transition-all ${
                            isCompleted ? "bg-success" : isOverdue ? "bg-expense" : "bg-secondary"
                          }`}
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                      <div className="flex items-center justify-between mt-2">
                        <span className="text-xs text-muted-foreground">
                          {formatCurrency(goal.currentAmount)} / {formatCurrency(goal.targetAmount)}
                        </span>
                        <span className="text-xs font-semibold text-foreground">
                          {pct}% {t("goals.progress")}
                        </span>
                      </div>
                      <p className="text-xs text-muted-foreground mt-0.5">
                        {t("goals.deadline")}: {formatDate(goal.targetDate)}
                      </p>
                    </div>
                  );
                })
              )}
            </div>
          </section>

          {/* Recent Transactions */}
          <section className="rounded-xl border border-border bg-card shadow-sm">
            <div className="flex items-center justify-between px-6 py-4 border-b border-border">
              <h2 className="text-sm font-semibold text-foreground">{t("transactions.title")}</h2>
              <RefreshCw
                size={14}
                className="text-muted-foreground cursor-pointer hover:text-foreground transition-colors"
                onClick={fetchData}
              />
            </div>
            <div className="divide-y divide-border">
              {loading ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <div key={i} className="px-6 py-3 flex items-center gap-3">
                    <Skeleton className="h-9 w-9 rounded-lg shrink-0" />
                    <div className="flex-1 space-y-1.5">
                      <Skeleton className="h-3.5 w-32" />
                      <Skeleton className="h-3 w-20" />
                    </div>
                    <Skeleton className="h-4 w-16" />
                  </div>
                ))
              ) : transactions.length === 0 ? (
                <p className="px-6 py-8 text-center text-sm text-muted-foreground">{t("transactions.empty")}</p>
              ) : (
                transactions.map((tx) => {
                  const isIncome = tx.type === "INCOME";
                  return (
                    <div key={tx.id} className="px-6 py-3 flex items-center gap-3">
                      <div className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-lg ${
                        isIncome ? "bg-income/10" : "bg-expense/10"
                      }`}>
                        {isIncome
                          ? <ArrowUpRight size={16} className="text-income" />
                          : <ArrowDownRight size={16} className="text-expense" />}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-foreground truncate">
                          {tx.description || (isIncome ? t("transactions.income") : t("transactions.expense"))}
                        </p>
                        <p className="text-xs text-muted-foreground">{formatDate(tx.transactionDate)}</p>
                      </div>
                      <p className={`text-sm font-semibold shrink-0 ${isIncome ? "text-income" : "text-expense"}`}>
                        {isIncome ? "+" : "-"}{formatCurrency(tx.amount)}
                      </p>
                    </div>
                  );
                })
              )}
            </div>
          </section>
        </div>
      </div>
    </PublicLayout>
  );
}
