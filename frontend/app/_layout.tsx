import {
  DarkTheme,
  DefaultTheme,
  ThemeProvider,
} from "@react-navigation/native";
import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import "react-native-reanimated";

import { useColorScheme } from "@/hooks/use-color-scheme";
import { useEffect } from "react";
import { ActivityIndicator } from "react-native";
import { ThemedView } from "@/components/themed-view";
import { useAuthStore } from "@/store/auth-store";

export const unstable_settings = {
  anchor: "(tabs)",
};

export default function RootLayout() {
  const colorScheme = useColorScheme();

  const initialized = useAuthStore((s) => s.initialized);
  const init = useAuthStore((s) => s.init);

  useEffect(() => {
    if (!initialized) {
      void init();
    }
  }, [initialized, init]);

  if (!initialized) {
    return (
      <ThemedView
        style={{
          flex: 1,
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <ActivityIndicator />
      </ThemedView>
    );
  }

  return (
    <ThemeProvider value={colorScheme === "dark" ? DarkTheme : DefaultTheme}>
      <Stack>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen
          name="modal"
          options={{ presentation: "modal", title: "Modal" }}
        />
        {/* puoi aggiungere qui registration-screen, ecc., se necessario */}
      </Stack>
      <StatusBar style="auto" />
    </ThemeProvider>
  );
}
