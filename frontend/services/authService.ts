import { api } from './api';
import Cookies from 'js-cookie';

export const authService = {
  async login(email: string, password: string) {
    const { data } = await api.post('/login', { email, password });
    return data as { access_token: string; refresh_token: string; expires_in: number };
  },

  async me() {
    const { data } = await api.get('/me');
    return data;
  },

  setTokens(accessToken: string, refreshToken: string, expiresIn: number, userId?: number) {
    Cookies.set('access_token', accessToken, { expires: expiresIn / 86400, secure: true, sameSite: 'lax' });
    Cookies.set('refresh_token', refreshToken, { expires: 7, secure: true, sameSite: 'lax' });
    if (userId) Cookies.set('user_id', String(userId), { expires: 7, secure: true, sameSite: 'lax' });
  },

  setGoogleToken(accessToken: string, userId?: number) {
    Cookies.set('access_token', accessToken, { expires: 1, secure: true, sameSite: 'lax' });
    if (userId) Cookies.set('user_id', String(userId), { expires: 1, secure: true, sameSite: 'lax' });
  },

  clearTokens() {
    Cookies.remove('access_token');
    Cookies.remove('refresh_token');
    Cookies.remove('user_id');
  },

  async logout() {
    try {
      await api.post('/logout');
    } catch (error) {
      console.error("Erro ao fazer logout na API", error);
    } finally {
      this.clearTokens();
    }
  },
};