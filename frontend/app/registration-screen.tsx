import { useState } from "react";
import { useAuthStore } from "@/store/auth-store";
import { StyleSheet } from "react-native";
import { ThemedView } from "@/components/themed-view";
import { ThemedText } from "@/components/themed-text";
import { useRouter } from "expo-router";

import {
  checkEmail,
  loginWithPassword,
  registerWithEmail,
  AuthError,
} from "@/api/auth";
import { EmailStepForm } from "@/components/auth/email-step-form";
import { ExistingUserForm } from "@/components/auth/existing-user-form";
import { NewUserForm } from "@/components/auth/new-user-form";

type Step = "email" | "existing-user" | "new-user";

export default function RegistrationScreen() {
  const router = useRouter();
  const [step, setStep] = useState<Step>("email");
  const [email, setEmail] = useState("");
  const [checkingEmail, setCheckingEmail] = useState(false);
  const [globalError, setGlobalError] = useState<string | null>(null);

  const loginFromAuthResponse = useAuthStore((s) => s.loginFromAuthResponse);

  const handleEmailSubmit = async ({ email }: { email: string }) => {
    setGlobalError(null);
    setCheckingEmail(true);

    try {
      const { exists } = await checkEmail(email);
      setEmail(email);
      setStep(exists ? "existing-user" : "new-user");
    } catch (err) {
      if (err instanceof AuthError) {
        setGlobalError(err.message);
      } else {
        setGlobalError("Errore durante la verifica dell’email.");
      }
    } finally {
      setCheckingEmail(false);
    }
  };

  const handleExistingUserSubmit = async ({
    password,
  }: {
    password: string;
  }) => {
    console.log("handleExistingUserSubmit called with:", { email, password: "***" });
    setGlobalError(null);
    try {
      console.log("Calling loginWithPassword...");
      const auth = await loginWithPassword({
        usernameOrEmail: email,
        password,
      });
      console.log("Login successful:", auth);

      await loginFromAuthResponse(auth);
      console.log("Auth response saved to store");
      // La Home si aggiorna automaticamente tramite lo store
    } catch (err) {
      console.error("Login error:", err);
      if (err instanceof AuthError) {
        setGlobalError(err.message || "Email o password non corretti.");
      } else {
        setGlobalError("Errore durante il login.");
      }
    }
  };

  const handleNewUserSubmit = async ({
    username,
    password,
  }: {
    username: string;
    password: string;
  }) => {
    setGlobalError(null);
    try {
      const auth = await registerWithEmail({ email, username, password });

      await loginFromAuthResponse(auth);
      // La Home si aggiorna automaticamente tramite lo store
    } catch (err) {
      if (err instanceof AuthError) {
        setGlobalError(err.message || "Registrazione fallita.");
      } else {
        setGlobalError("Errore durante la registrazione.");
      }
    }
  };

  return (
    <ThemedView style={styles.container}>
      <ThemedText type="title" style={styles.title}>
        Registrazione
      </ThemedText>

      {globalError && (
        <ThemedText style={styles.globalError}>{globalError}</ThemedText>
      )}

      {step === "email" && (
        <EmailStepForm
          defaultEmail={email}
          loading={checkingEmail}
          onSubmit={handleEmailSubmit}
        />
      )}

      {step === "existing-user" && (
        <ExistingUserForm email={email} onSubmit={handleExistingUserSubmit} />
      )}

      {step === "new-user" && (
        <NewUserForm email={email} onSubmit={handleNewUserSubmit} />
      )}
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    justifyContent: "center",
  },
  title: {
    textAlign: "center",
    marginBottom: 16,
  },
  globalError: {
    marginBottom: 12,
    fontSize: 14,
  },
});
