import { create } from "zustand";
import type { AuthResponse } from "@/api/auth";
import { saveAuth, loadAuth, clearAuth } from "@/storage/auth-storage";
import { setAuthToken } from "@/api/client";

type AuthUser = AuthResponse["user"];

type AuthState = {
  user: AuthUser | null;
  accessToken: string | null;
  refreshToken: string | null;
  initialized: boolean;

  init: () => Promise<void>;
  loginFromAuthResponse: (auth: AuthResponse) => Promise<void>;
  logout: () => Promise<void>;
};

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  accessToken: null,
  refreshToken: null,
  initialized: false,

  init: async () => {
    if (get().initialized) return;

    const { accessToken, refreshToken, user } = await loadAuth();

    if (accessToken && user) {
      set({ accessToken, refreshToken, user });
      setAuthToken(accessToken);
    }

    set({ initialized: true });
  },

  loginFromAuthResponse: async (auth: AuthResponse) => {
    set({
      user: auth.user,
      accessToken: auth.tokens.accessToken,
      refreshToken: auth.tokens.refreshToken ?? null,
    });

    setAuthToken(auth.tokens.accessToken);

    await saveAuth({
      accessToken: auth.tokens.accessToken,
      refreshToken: auth.tokens.refreshToken,
      user: auth.user,
    });
  },

  logout: async () => {
    set({
      user: null,
      accessToken: null,
      refreshToken: null,
    });
    setAuthToken(null);
    await clearAuth();
  },
}));
