import createMiddleware from 'next-intl/middleware';
import { routing } from './i18n/routing';
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const intlMiddleware = createMiddleware(routing);

const protectedRoutes = ['/dashboard', '/transactions', '/goals', '/categories'];

const authRoutes = ['/login', '/register'];

export default function middleware(req: NextRequest) {
  const token = req.cookies.get('access_token')?.value;
  const currentPath = req.nextUrl.pathname;

  const locale = routing.locales.find(loc => currentPath.startsWith(`/${loc}`)) || routing.defaultLocale;

  const isProtectedRoute = protectedRoutes.some((route) => currentPath.includes(route));
  const isAuthRoute = authRoutes.some((route) => currentPath.includes(route));

  if (isProtectedRoute && !token) {
    const loginUrl = new URL(`/${locale}/login`, req.url);
    return NextResponse.redirect(loginUrl);
  }

  if (isAuthRoute && token) {
    const dashboardUrl = new URL(`/${locale}/dashboard`, req.url);
    return NextResponse.redirect(dashboardUrl);
  }

  return intlMiddleware(req);
}

export const config = {
  matcher: ['/', '/(pt|en)/:path*']
};