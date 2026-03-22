"use client";

import { useEffect, useRef, useState } from "react";
import { useSearchParams } from "next/navigation";
import { useRouter } from "@/i18n/routing";
import { useTranslations } from "next-intl";
import { useAuth } from "@/hooks/useAuth";
import { authService } from "@/services/authService";
import { Wallet, Loader2, AlertCircle } from "lucide-react";
import { Link } from "@/i18n/routing";

export default function GoogleCallbackPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const t = useTranslations("Auth");
  const { loginContext } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const processed = useRef(false);

  useEffect(() => {
    if (processed.current) return;
    processed.current = true;

    const token = searchParams.get("token");
    const errorParam = searchParams.get("error");

    Promise.resolve().then(() => {
      if (errorParam) {
        setError(t("errors.google_error"));
        return;
      }
      if (!token) {
        setError(t("errors.google_no_token"));
        return;
      }

      authService.setGoogleToken(token);

      authService.me()
        .then((userData) => {
          loginContext(token, "", 86400, userData);
          router.push("/dashboard");
        })
        .catch(() => {
          authService.clearTokens();
          setError(t("errors.google_load_user"));
        });
    });
  }, [loginContext, router, searchParams, t]);

  if (error) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-background px-4">
        <div className="flex flex-col items-center gap-4 text-center max-w-sm">
          <div className="h-12 w-12 rounded-full bg-destructive/10 flex items-center justify-center">
            <AlertCircle className="text-destructive" size={24} />
          </div>
          <h1 className="text-lg font-semibold text-foreground">
            {t("google_callback.error_title")}
          </h1>
          <p className="text-sm text-muted-foreground">{error}</p>
          <Link
            href="/login"
            className="text-sm text-primary font-medium hover:underline focus:outline-none focus:ring-2 focus:ring-ring rounded-sm"
          >
            {t("google_callback.back_to_login")}
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-background gap-4">
      <div className="h-12 w-12 rounded-full bg-primary flex items-center justify-center text-primary-foreground shadow-sm">
        <Wallet size={24} />
      </div>
      <div className="flex items-center gap-2 text-muted-foreground text-sm">
        <Loader2 className="animate-spin" size={16} />
        <span>{t("google_callback.loading")}</span>
      </div>
    </div>
  );
}
