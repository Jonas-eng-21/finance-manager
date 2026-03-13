import { api } from './api';
import Cookies from 'js-cookie';

export const authService = {
  async me() {
    const { data } = await api.get('/me');
    return data;
  },

  setTokens(accessToken: string, refreshToken: string, expiresIn: number) {
    Cookies.set('access_token', accessToken, { expires: expiresIn / 86400, secure: true, sameSite: 'lax' });
    Cookies.set('refresh_token', refreshToken, { expires: 7, secure: true, sameSite: 'lax' });
  },

  clearTokens() {
    Cookies.remove('access_token');
    Cookies.remove('refresh_token');
  },

  async logout() {
    try {
      await api.post('/logout');
    } catch (error) {
      console.error("Erro ao fazer logout na API", error);
    } finally {
      this.clearTokens();
    }
  }
};