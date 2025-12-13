import { useState } from "react";
import { StyleSheet } from "react-native";
import { ThemedView } from "@/components/themed-view";
import { ThemedText } from "@/components/themed-text";

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
  const [step, setStep] = useState<Step>("email");
  const [email, setEmail] = useState("");
  const [checkingEmail, setCheckingEmail] = useState(false);
  const [globalError, setGlobalError] = useState<string | null>(null);

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
    setGlobalError(null);
    try {
      const auth = await loginWithPassword({ email, password });
      // TODO: salva token + naviga al gioco
      console.log("Logged in:", auth.user);
    } catch (err) {
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
      // TODO: salva token + naviga
      console.log("Registered:", auth.user);
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
