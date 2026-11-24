import apiClient from '@/api/axios';
import { API_ENDPOINTS } from '@/constants/apiEndpoints';
import type {
  LoginRequest,
  CreateUserDto,
  TokenResponse,
  UserResponse,
  RequestRefreshToken
} from '@/types/auth';

export const authService = {
  async login(credentials: LoginRequest): Promise<TokenResponse> {
    const { data } = await apiClient.post<TokenResponse>(
      API_ENDPOINTS.AUTH.LOGIN_V1,
      credentials
    );
    if (data.access_token) {
      localStorage.setItem('access_token', data.access_token);
      localStorage.setItem('refresh_token', data.refresh_token);
    }
    return data;
  },

  async signup(payload: CreateUserDto): Promise<UserResponse> {
    const { data } = await apiClient.post<UserResponse>(
      API_ENDPOINTS.AUTH.SIGNUP_V1,
      payload
    );
    // Your UserResponse includes tokens, so we can auto-login immediately!
    if (data.tokens?.access_token) {
      localStorage.setItem('access_token', data.tokens.access_token);
      localStorage.setItem('refresh_token', data.tokens.refresh_token);
    }
    return data;
  },

  async logout(): Promise<void> {
    const refreshToken = localStorage.getItem('refresh_token') || '';
    try {
      await apiClient.post(API_ENDPOINTS.AUTH.LOGOUT_V1, { refreshToken });
    } finally {
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
    }
  },

  // async refreshToken(payload: RequestRefreshToken) : Promise<TokenResponse>{
  //   const {data} = await apiClient.post<RequestRefreshToken>(
  //     API_ENDPOINTS.AUTH.REFRESH_TOKEN_V1,
  //     payload
  //   );
  //   if (data.access_token) {
  //     localStorage.setItem('access_token', data.access_token);
  //     localStorage.setItem('refresh_token', data.refresh_token);
  //   }
  //   return data;
  // }
};
