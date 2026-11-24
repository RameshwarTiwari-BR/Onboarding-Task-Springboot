export interface LoginRequest {
  email: string;
  password: string;
}

export interface CreateUserDto {
  email: string;
  firstName: string;
  lastName: string;
  password: string; // min 8 chars
}

export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  token_type: string;
}

export interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  status: 'ACTIVE' | 'DISABLED';
  createdAt: string;
  tokens?: TokenResponse;
}

export interface LogoutRequest {
  refreshToken: string;
}


export interface RequestRefreshToken{
  refresh_token : string
}