import axios from "axios";

const API_BASE_URL =
  process.env.EXPO_PUBLIC_API_URL ?? "http://localhost:8080/api";

let currentAccessToken: string | null = null;

export function setAuthToken(token: string | null) {
  currentAccessToken = token;
}

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

apiClient.interceptors.request.use((config) => {
  if (currentAccessToken) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${currentAccessToken}`;
  }
  return config;
});
