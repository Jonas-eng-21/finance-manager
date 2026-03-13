"use client";

import { useTranslations, useLocale } from "next-intl";
import { Link, usePathname, useRouter } from "@/i18n/routing";
import { Button } from "@/components/ui/button";
import {
  Sheet,
  SheetContent,
  SheetTrigger,
  SheetTitle,
} from "@/components/ui/sheet";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Menu, Wallet, Globe } from "lucide-react";
import { ThemeToggle } from "@/components/ui/ThemeToggle";

export function Navbar() {
  const t = useTranslations("Navbar");
  const locale = useLocale();
  const router = useRouter();
  const pathname = usePathname();

  const changeLanguage = (nextLocale: string) => {
    router.replace(pathname, { locale: nextLocale });
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b border-border bg-background/80 backdrop-blur-md">
      <div className="container mx-auto flex h-16 items-center justify-between px-6">
        <Link href="/" className="flex items-center gap-2">
          <div className="rounded-lg bg-primary p-1.5 text-primary-foreground">
            <Wallet size={20} />
          </div>
          <span className="text-xl font-bold text-foreground tracking-tight">
            {t("manager")}
          </span>
        </Link>

        <nav className="hidden md:flex items-center gap-4">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon" aria-label={t("language")}>
                <Globe className="h-5 w-5 text-foreground" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              align="end"
              className="bg-background border-border"
            >
              <DropdownMenuItem
                onClick={() => changeLanguage("pt")}
                className={`cursor-pointer ${locale === "pt" ? "bg-primary/10 font-bold text-primary" : ""}`}
              >
                {t("pt")}
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => changeLanguage("en")}
                className={`cursor-pointer ${locale === "en" ? "bg-primary/10 font-bold text-primary" : ""}`}
              >
                {t("en")}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <ThemeToggle />

          <Button
            variant="ghost"
            asChild
            className="text-foreground font-medium"
          >
            <Link href="/login">{t("login")}</Link>
          </Button>
          <Button
            asChild
            className="bg-primary text-primary-foreground hover:bg-primary/90 rounded-full px-6"
          >
            <Link href="/register">{t("register")}</Link>
          </Button>
        </nav>

        <div className="md:hidden flex items-center gap-2">
          <ThemeToggle />

          <Sheet>
            <SheetTrigger asChild>
              <Button variant="ghost" size="icon">
                <Menu className="h-6 w-6 text-foreground" />
              </Button>
            </SheetTrigger>
            <SheetContent
              side="right"
              className="w-[85vw] sm:w-87.5 h-full flex flex-col bg-background border-border p-8"
            >
              <SheetTitle className="text-left mt-4 flex items-center gap-2">
                <Wallet className="text-primary" size={20} />
                <span className="font-bold text-foreground">
                  {t("manager")}
                </span>
              </SheetTitle>

              <div className="flex flex-col gap-4 mt-8">
                <Button
                  variant="ghost"
                  className="w-full justify-start text-lg text-foreground"
                  asChild
                >
                  <Link href="/login">{t("login")}</Link>
                </Button>
                <Button
                  className="w-full justify-start text-lg bg-primary text-primary-foreground rounded-full"
                  asChild
                >
                  <Link href="/register">{t("register")}</Link>
                </Button>
              </div>

              <div className="mt-auto pb-4 flex flex-col gap-4 border-t border-border pt-6">
                <span className="text-sm font-semibold text-muted-foreground uppercase tracking-wider">
                  {t("language")}
                </span>
                <div className="flex gap-2">
                  <Button
                    variant={locale === "pt" ? "default" : "outline"}
                    onClick={() => changeLanguage("pt")}
                    className={`flex-1 ${locale === "pt" ? "bg-primary text-primary-foreground" : "text-foreground"}`}
                  >
                    {t("pt")}
                  </Button>
                  <Button
                    variant={locale === "en" ? "default" : "outline"}
                    onClick={() => changeLanguage("en")}
                    className={`flex-1 ${locale === "en" ? "bg-primary text-primary-foreground" : "text-foreground"}`}
                  >
                    {t("en")}
                  </Button>
                </div>
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </header>
  );
}
