// front/storage/auth-storage.ts
import * as SecureStore from "expo-secure-store";
import { Platform } from "react-native";
import type { AuthResponse } from "@/api/auth";

const ACCESS_TOKEN_KEY = "accessToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const USER_DATA_KEY = "userData";

type UserData = AuthResponse["user"];

// Cross-platform storage wrapper
const storage = {
  async setItem(key: string, value: string): Promise<void> {
    if (Platform.OS === "web") {
      localStorage.setItem(key, value);
    } else {
      await SecureStore.setItemAsync(key, value);
    }
  },
  async getItem(key: string): Promise<string | null> {
    if (Platform.OS === "web") {
      return localStorage.getItem(key);
    } else {
      return await SecureStore.getItemAsync(key);
    }
  },
  async removeItem(key: string): Promise<void> {
    if (Platform.OS === "web") {
      localStorage.removeItem(key);
    } else {
      await SecureStore.deleteItemAsync(key);
    }
  },
};

export async function saveAuth(params: {
  accessToken: string;
  refreshToken?: string;
  user: UserData;
}) {
  await storage.setItem(ACCESS_TOKEN_KEY, params.accessToken);
  if (params.refreshToken) {
    await storage.setItem(REFRESH_TOKEN_KEY, params.refreshToken);
  }
  await storage.setItem(USER_DATA_KEY, JSON.stringify(params.user));
}

// Backward compatibility
export async function saveTokens(params: {
  accessToken: string;
  refreshToken?: string;
}) {
  await storage.setItem(ACCESS_TOKEN_KEY, params.accessToken);
  if (params.refreshToken) {
    await storage.setItem(REFRESH_TOKEN_KEY, params.refreshToken);
  }
}

export async function loadAuth(): Promise<{
  accessToken: string | null;
  refreshToken: string | null;
  user: UserData | null;
}> {
  const accessToken = await storage.getItem(ACCESS_TOKEN_KEY);
  const refreshToken = await storage.getItem(REFRESH_TOKEN_KEY);
  const userDataStr = await storage.getItem(USER_DATA_KEY);

  let user: UserData | null = null;
  if (userDataStr) {
    try {
      user = JSON.parse(userDataStr);
    } catch (e) {
      console.error("Failed to parse user data:", e);
    }
  }

  return { accessToken, refreshToken, user };
}

// Backward compatibility
export async function loadTokens(): Promise<{
  accessToken: string | null;
  refreshToken: string | null;
}> {
  const accessToken = await storage.getItem(ACCESS_TOKEN_KEY);
  const refreshToken = await storage.getItem(REFRESH_TOKEN_KEY);
  return { accessToken, refreshToken };
}

export async function clearAuth() {
  await storage.removeItem(ACCESS_TOKEN_KEY);
  await storage.removeItem(REFRESH_TOKEN_KEY);
  await storage.removeItem(USER_DATA_KEY);
}

// Backward compatibility
export async function clearTokens() {
  await clearAuth();
}
