import axios from "axios";
import { apiClient } from "./client";

export class AuthError extends Error {
  status?: number;
  constructor(message: string, status?: number) {
    super(message);
    this.name = "AuthError";
    this.status = status;
  }
}

function toAuthError(error: unknown): AuthError {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;
    const data = error.response?.data as any;
    const message =
      data?.message ||
      data?.error ||
      error.message ||
      "Errore di autenticazione";

    return new AuthError(message, status);
  }

  return new AuthError("Errore di rete o server non raggiungibile");
}

export type ApiResponse<T> = {
  status: number;
  data: T;
  message?: string;
};

export type AuthTokens = {
  accessToken: string;
  tokenType: string;
};

export async function checkEmail(email: string): Promise<{ exists: boolean }> {
  try {
    const res = await apiClient.post<ApiResponse<{ exists: boolean }>>(
      "/auth/check-email",
      { email }
    );
    return res.data.data;
  } catch (error) {
    throw toAuthError(error);
  }
}

export async function loginWithPassword(params: {
  usernameOrEmail: string;
  password: string;
}): Promise<AuthTokens> {
  try {
    const res = await apiClient.post<ApiResponse<AuthTokens>>(
      "/auth/login",
      params
    );
    return res.data.data;
  } catch (error) {
    throw toAuthError(error);
  }
}

export async function registerWithEmail(params: {
  email: string;
  username: string;
  password: string;
}): Promise<AuthTokens> {
  try {
    const res = await apiClient.post<ApiResponse<AuthTokens>>(
      "/auth/register",
      params
    );
    return res.data.data;
  } catch (error) {
    throw toAuthError(error);
  }
}
