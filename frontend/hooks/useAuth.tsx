"use client";

import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { authService } from "@/services/authService";
import Cookies from 'js-cookie';

interface User {
  id: number;
  name: string;
  email: string;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  loginContext: (accessToken: string, refreshToken: string, expiresIn: number, userData?: User) => Promise<void>;
  logoutContext: () => void;
}

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadUser = async () => {
      const token = Cookies.get('access_token');
      if (token) {
        try {
          const userData = await authService.me();
          setUser(userData);
        } catch {
          authService.clearTokens();
        }
      }
      setIsLoading(false);
    };

    loadUser();
  }, []);

  const loginContext = async (accessToken: string, refreshToken: string, expiresIn: number, userData?: User) => {
    if (refreshToken) {
      authService.setTokens(accessToken, refreshToken, expiresIn, userData?.id);
    }
    if (userData) {
      setUser(userData);
    } else {
      const data = await authService.me();
      setUser(data);
      Cookies.set('user_id', String(data.id), { expires: 7, secure: true, sameSite: 'lax' });
    }
  };

  const logoutContext = async () => {
    await authService.logout();
    setUser(null);
    window.location.href = '/login';
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, isLoading, loginContext, logoutContext }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);