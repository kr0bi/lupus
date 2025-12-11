import { StyleSheet } from "react-native";
import { ThemedView } from "@/components/themed-view";
import NewAccountScreen from "@/app/registration/new-account-screen";

export default function RegistrationScreen() {
  return (
    <ThemedView style={styles.container}>
      <NewAccountScreen />
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    maxWidth: 400,
    width: "100%",
    alignSelf: "center",
    padding: 16,
  },
});
