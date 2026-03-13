import { useTranslations } from "next-intl";
import { Link } from "@/i18n/routing";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { PublicLayout } from "@/components/layout/PublicLayout";
import { TrendingUp, BarChart3, Zap } from "lucide-react";

export default function HomePage() {
  const t = useTranslations("Home");

  return (
    <PublicLayout>
      <section className="px-6 pt-20 pb-24 text-center md:pt-32 md:pb-32">
        <h1 className="text-5xl font-extrabold tracking-tight text-foreground sm:text-7xl">
          {t("hero.title")} <br className="hidden sm:block" />
          <span className="text-primary">{t("hero.highlight")}</span>
        </h1>
        <p className="mx-auto mt-6 max-w-2xl text-lg text-muted-foreground">
          {t("hero.description")}
        </p>
        <div className="mt-10 flex flex-col sm:flex-row justify-center gap-4">
          <Button size="lg" className="h-12 px-8 text-md rounded-full bg-primary text-primary-foreground hover:bg-primary/90" asChild>
            <Link href="/register">{t("hero.btnStart")}</Link>
          </Button>
          <Button size="lg" variant="secondary" className="h-12 px-8 text-md rounded-full bg-secondary text-secondary-foreground hover:bg-secondary/80" asChild>
            <Link href="/login">{t("hero.btnLogin")}</Link>
          </Button>
        </div>
      </section>

      <section className="container mx-auto px-6 pb-24">
        <div className="grid gap-6 md:grid-cols-3">
          <FeatureCard 
            icon={<TrendingUp size={24} className="text-primary" />}
            title={t("features.track.title")}
            description={t("features.track.description")}
          />
          <FeatureCard 
            icon={<BarChart3 size={24} className="text-primary" />}
            title={t("features.analysis.title")}
            description={t("features.analysis.description")}
          />
          <FeatureCard 
            icon={<Zap size={24} className="text-primary" />}
            title={t("features.fast.title")}
            description={t("features.fast.description")}
          />
        </div>
      </section>

      <section className="container mx-auto px-6 pb-24">
        <div className="rounded-4xl bg-primary px-6 py-16 text-center text-primary-foreground shadow-xl">
          <h2 className="text-3xl font-bold sm:text-4xl">{t("cta.title")}</h2>
          <p className="mt-4 text-lg text-primary-foreground/80">{t("cta.description")}</p>
          <Button size="lg" variant="secondary" className="mt-8 rounded-full font-bold bg-background text-primary hover:bg-background/90" asChild>
            <Link href="/register">{t("cta.btn")}</Link>
          </Button>
        </div>
      </section>
    </PublicLayout>
  );
}

function FeatureCard({ icon, title, description }: { icon: React.ReactNode, title: string, description: string }) {
  return (
    <Card className="border-border bg-card text-card-foreground shadow-sm hover:shadow-md transition-all rounded-2xl">
      <CardHeader>
        <div className="mb-4 w-fit rounded-xl bg-primary/10 p-3">
          {icon}
        </div>
        <CardTitle className="text-xl font-bold">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-muted-foreground">{description}</p>
      </CardContent>
    </Card>
  );
}