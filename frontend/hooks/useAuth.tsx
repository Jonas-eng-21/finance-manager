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
  loginContext: (accessToken: string, refreshToken: string, expiresIn: number, userData?: User) => void;
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
        } catch (error) {
          authService.clearTokens();
        }
      }
      setIsLoading(false);
    };

    loadUser();
  }, []);

  const loginContext = (accessToken: string, refreshToken: string, expiresIn: number, userData?: User) => {
    authService.setTokens(accessToken, refreshToken, expiresIn);
    if (userData) {
      setUser(userData);
    } else {
      authService.me().then(setUser);
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