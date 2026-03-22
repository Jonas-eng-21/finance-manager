import axios from 'axios';
import Cookies from 'js-cookie';


export const financialApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_FINANCIAL_API_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

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
