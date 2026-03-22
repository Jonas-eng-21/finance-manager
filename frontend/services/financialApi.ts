import axios from 'axios';
import Cookies from 'js-cookie';

/**
 * Instância Axios dedicada ao service-financial (porta 8003).
 * Espelha a configuração do api.ts (service-identity), mas aponta
 * para NEXT_PUBLIC_FINANCIAL_API_URL e injeta o header X-User-Id
 * automaticamente via interceptor.
 *
 * O userId é lido do cookie 'user_id' — populado em setUserId()
 * após o login bem-sucedido.
 */
export const financialApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_FINANCIAL_API_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// Injeta Bearer token e X-User-Id em toda requisição
financialApi.interceptors.request.use(
  (config) => {
    const token = Cookies.get('access_token');
    const userId = Cookies.get('user_id');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    if (userId) {
      config.headers['X-User-Id'] = userId;
    }

    return config;
  },
  (error) => Promise.reject(error)
);
