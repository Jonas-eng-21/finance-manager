import { Navbar } from "./Navbar";

export function PublicLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="relative flex min-h-screen flex-col bg-background text-foreground">
      <Navbar />
      <main className="flex-1">{children}</main>
    </div>
  );
}