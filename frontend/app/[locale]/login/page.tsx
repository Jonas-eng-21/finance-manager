"use client";

import { useState } from "react";
import { useTranslations } from "next-intl";
import { Link, useRouter } from "@/i18n/routing";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Wallet, Eye, EyeOff, Loader2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { useAuth } from "@/hooks/useAuth";

export default function LoginPage() {
  const t = useTranslations("Auth");
  const router = useRouter();
  const { loginContext } = useAuth();
  
  const [showPassword, setShowPassword] = useState(false);
  const [globalError, setGlobalError] = useState<string | null>(null);

  const loginSchema = z.object({
    email: z.string().email({ message: t("errors.invalid_email") }),
    password: z.string().min(8, { message: t("errors.min_password") }),
  });

  type LoginFormValues = z.infer<typeof loginSchema>;

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: "", password: "" },
  });

const onSubmit = async (data: LoginFormValues) => {
    setGlobalError(null);
    
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
        body: JSON.stringify(data),
      });

      const responseData = await response.json();

      if (!response.ok) {
        if (response.status === 422 && responseData.errors) {
          Object.keys(responseData.errors).forEach((key) => {
            setError(key as keyof LoginFormValues, {
              type: "server",
              message: responseData.errors[key][0],
            });
          });
          return;
        }

        setGlobalError(responseData.error || t("errors.generic"));
        return;
      }

      loginContext(responseData.access_token, responseData.refresh_token, responseData.expires_in);
      
      router.push("/dashboard");

    } catch (error) {
      setGlobalError(t("errors.generic"));
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-background px-4 py-8">
      
      <div className="flex flex-col items-center mb-8">
        <div className="h-12 w-12 rounded-full bg-primary flex items-center justify-center text-primary-foreground shadow-sm">
          <Wallet size={24} />
        </div>
        <h1 className="text-xl font-semibold text-foreground mt-4 tracking-tight">
          {t("login.manager")}
        </h1>
      </div>

      <Card className="w-full max-w-md bg-card text-card-foreground border-border shadow-sm rounded-xl">
        <CardHeader className="pb-4 pt-8 px-8">
          <h2 className="text-xl font-semibold text-foreground">
            {t("login.title")}
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            {t("login.subtitle")}
          </p>
        </CardHeader>

        <CardContent className="px-8 pb-8">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            
            {globalError && (
              <div className="p-3 rounded-md bg-destructive/10 border border-destructive/20 text-destructive text-sm font-medium">
                {globalError}
              </div>
            )}

            {/* CAMPO EMAIL */}
            <div className="space-y-2">
              <Label htmlFor="email" className="text-foreground">
                {t("login.email_label")}
              </Label>
              <Input
                id="email"
                type="email"
                placeholder={t("login.email_placeholder")}
                className={`bg-background ${errors.email ? "border-destructive focus-visible:ring-destructive" : ""}`}
                {...register("email")}
              />
              {errors.email && (
                <p className="text-xs text-destructive font-medium">{errors.email.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label htmlFor="password" className="text-foreground">
                  {t("login.password_label")}
                </Label>
                <Link href="/forgot-password" className="text-xs text-primary font-medium hover:underline focus:outline-none focus:ring-2 focus:ring-ring rounded-sm">
                  {t("login.forgot_password")}
                </Link>
              </div>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder={t("login.password_placeholder")}
                  className={`bg-background pr-10 ${errors.password ? "border-destructive focus-visible:ring-destructive" : ""}`}
                  {...register("password")}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground focus:outline-none"
                  aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.password && (
                <p className="text-xs text-destructive font-medium">{errors.password.message}</p>
              )}
            </div>

            <Button
              type="submit"
              disabled={isSubmitting}
              className="w-full h-10 bg-primary text-primary-foreground font-medium mt-2 transition-all hover:opacity-90 disabled:opacity-70"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  {t("login.submitting")}
                </>
              ) : (
                t("login.submit")
              )}
            </Button>
          </form>

          <div className="flex items-center gap-3 my-6">
            <Separator className="flex-1" />
            <span className="text-xs text-muted-foreground uppercase tracking-wide font-medium">
              {t("login.divider")}
            </span>
            <Separator className="flex-1" />
          </div>

          <Button
            type="button"
            variant="outline"
            className="w-full h-10 bg-background hover:bg-muted text-foreground font-medium flex items-center justify-center gap-2"
            onClick={() => window.location.href = `${process.env.NEXT_PUBLIC_API_URL}/auth/google/redirect`}
          >
            <svg viewBox="0 0 24 24" className="h-5 w-5" aria-hidden="true">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
            </svg>
            {t("login.google_btn")}
          </Button>

          <div className="mt-8 text-center text-sm text-muted-foreground">
            {t("login.no_account")}{" "}
            <Link href="/register" className="text-primary font-medium hover:underline focus:outline-none focus:ring-2 focus:ring-ring rounded-sm">
              {t("login.create_account")}
            </Link>
          </div>
        </CardContent>
      </Card>

      <p className="mt-10 text-xs text-muted-foreground text-center">
        {t("login.footer")}
      </p>
    </div>
  );
}