import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import router from '@/router';
import { toast } from 'vue3-toastify';
import { API_ENDPOINTS } from '@/constants/apiEndpoints';
import type { TokenResponse } from '@/types/auth';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 1. Attach access_token to outgoing requests
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Queue state for concurrent 401 requests
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (err: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((promise) => {
    if (error) {
      promise.reject(error);
    } else if (token) {
      promise.resolve(token);
    }
  });
  failedQueue = [];
};

// 2. Catch 401 responses and silently refresh the token
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Only attempt refresh on 401, if we haven't retried yet, and NOT on login/refresh routes
    if (
      error.response?.status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !originalRequest.url?.includes(API_ENDPOINTS.AUTH.LOGIN_V1) &&
      !originalRequest.url?.includes(API_ENDPOINTS.AUTH.REFRESH_V1)
    ) {
      const refresh_token = localStorage.getItem('refresh_token');

      if (!refresh_token) {
        localStorage.removeItem('access_token');
        router.push('/');
        return Promise.reject(error);
      }

      // If another request is already refreshing, wait in queue
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({
            resolve: (newToken: string) => {
              if (originalRequest.headers) {
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
              }
              resolve(apiClient(originalRequest));
            },
            reject,
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Use raw axios.post so the expired Bearer header is NOT attached
        const { data } = await axios.post<TokenResponse>(
          `${import.meta.env.VITE_API_BASE_URL}${API_ENDPOINTS.AUTH.REFRESH_V1}`,
          { refresh_token }
        );

        localStorage.setItem('access_token', data.access_token);
        if (data.refresh_token) {
          localStorage.setItem('refresh_token', data.refresh_token);
        }

        // Retry all queued requests with the new token
        processQueue(null, data.access_token);

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${data.access_token}`;
        }
        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        toast.warning('Session expired. Please log in again.');
        router.push('/');
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
