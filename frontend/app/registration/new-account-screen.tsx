import { ThemedText } from "@/components/themed-text";
import { Controller, useForm } from "react-hook-form";
import { ThemedTextInput } from "@/components/themed-text-input";
import { ThemedButton } from "@/components/themed-button";
import React from "react";
import { StyleSheet } from "react-native";
import {
  RegistrationFormValues,
  registrationSchema,
} from "@/schemas/registration";
import { zodResolver } from "@hookform/resolvers/zod";

export default function NewAccountScreen() {
  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegistrationFormValues>({
    resolver: zodResolver(registrationSchema),
    defaultValues: {
      email: "",
      password: "",
      username: "",
    },
  });

  const onSubmit = async (values: RegistrationFormValues) => {
    // Call your API here
    // await api.register(values);
    console.log("Register with:", values);
  };
  return (
    <>
      <ThemedText type="title" style={styles.title}>
        Registrati
      </ThemedText>

      {/* Email */}
      <ThemedText style={styles.label}>Email</ThemedText>
      <Controller
        control={control}
        name="email"
        render={({ field: { onChange, onBlur, value } }) => (
          <ThemedTextInput
            style={[styles.input, errors.email && styles.inputError]}
            onBlur={onBlur}
            onChangeText={onChange}
            value={value}
            keyboardType="email-address"
            autoCapitalize="none"
            placeholder="you@example.com"
          />
        )}
      />
      {errors.email && (
        <ThemedText style={styles.error}>{errors.email.message}</ThemedText>
      )}

      {/* Password */}
      <ThemedText style={styles.label}>Password</ThemedText>
      <Controller
        control={control}
        name="password"
        render={({ field: { onChange, onBlur, value } }) => (
          <ThemedTextInput
            style={[styles.input, errors.password && styles.inputError]}
            onBlur={onBlur}
            onChangeText={onChange}
            value={value}
            secureTextEntry
            autoCapitalize="none"
            placeholder="••••••••"
          />
        )}
      />
      {errors.password && (
        <ThemedText style={styles.error}>{errors.password.message}</ThemedText>
      )}

      <ThemedText style={styles.label}>Username</ThemedText>
      <Controller
        control={control}
        name="username"
        render={({ field: { onChange, onBlur, value } }) => (
          <ThemedTextInput
            style={[styles.input, errors.username && styles.inputError]}
            onBlur={onBlur}
            onChangeText={onChange}
            placeholder="Username visibile in gioco"
            value={value}
          />
        )}
      />
      {errors.username && (
        <ThemedText style={styles.error}>{errors.username.message}</ThemedText>
      )}

      <ThemedButton
        title="Crea un account"
        onPress={handleSubmit(onSubmit)}
        loading={isSubmitting}
      />
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    maxWidth: 400,
    width: "100%",
    alignSelf: "center",
    padding: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: "600",
    marginBottom: 16,
    textAlign: "center",
  },
  label: {
    marginTop: 8,
    marginBottom: 4,
    fontSize: 14,
    fontWeight: "500",
  },
  input: {
    borderWidth: 1,
    borderRadius: 6,
    paddingHorizontal: 10,
    paddingVertical: 8,
    fontSize: 16,
  },
  inputError: {
    borderColor: "red",
  },
  error: {
    color: "red",
    fontSize: 12,
    marginTop: 4,
  },
  button: {
    marginTop: 16,
    paddingVertical: 10,
    borderRadius: 6,
    borderWidth: 1,
    alignItems: "center",
  },
  buttonText: {
    fontSize: 16,
    fontWeight: "600",
  },
});
