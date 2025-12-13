import { Image } from "expo-image";
import { StyleSheet } from "react-native";

import { HelloWave } from "@/components/hello-wave";
import ParallaxScrollView from "@/components/parallax-scroll-view";
import { ThemedText } from "@/components/themed-text";
import { ThemedView } from "@/components/themed-view";
import { ThemedButton } from "@/components/themed-button";
import RegistrationScreen from "@/app/registration-screen";
import { useAuthStore } from "@/store/auth-store";

export default function HomeScreen() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  return (
    <ParallaxScrollView
      headerBackgroundColor={{ light: "#A1CEDC", dark: "#1D3D47" }}
      headerImage={
        <Image
          source={require("@/assets/images/partial-react-logo.png")}
          style={styles.reactLogo}
        />
      }
    >
      {user ? (
        <>
          <ThemedView style={styles.header}>
            <ThemedView style={styles.headerRow}>
              <ThemedText type="subtitle" style={styles.welcome}>
                Benvenuto {user.username}!
              </ThemedText>
              <ThemedButton
                title="Disconnettiti"
                onPress={logout}
                style={styles.logoutButton}
              />
            </ThemedView>
          </ThemedView>
          <ThemedView style={styles.lobbyContent}>
            {/* Contenuto della lobby */}
          </ThemedView>
        </>
      ) : (
        <>
          <ThemedView style={styles.titleContainer}>
            <ThemedText type="title">Benvenuto!</ThemedText>
            <HelloWave />
          </ThemedView>
          <ThemedView style={styles.stepContainer}>
            <RegistrationScreen />
          </ThemedView>
        </>
      )}
    </ParallaxScrollView>
  );
}

const styles = StyleSheet.create({
  titleContainer: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },
  stepContainer: {
    gap: 8,
    marginBottom: 8,
  },
  reactLogo: {
    height: 178,
    width: 290,
    bottom: 0,
    left: 0,
    position: "absolute",
  },
  header: {
    paddingVertical: 16,
  },
  headerRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    gap: 16,
  },
  welcome: {
    fontSize: 18,
    flex: 1,
  },
  logoutButton: {
    marginTop: 0,
    paddingVertical: 8,
    paddingHorizontal: 16,
    minWidth: 120,
  },
  lobbyContent: {
    gap: 16,
    marginBottom: 16,
  },
});
