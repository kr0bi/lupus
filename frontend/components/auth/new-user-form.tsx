import { StyleSheet } from "react-native";
import { useForm, Controller } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { ThemedText } from "@/components/themed-text";
import { ThemedTextInput } from "@/components/themed-text-input";
import { ThemedButton } from "@/components/themed-button";

const schema = z.object({
  username: z
    .string()
    .min(2, "Almeno 2 caratteri")
    .max(20, "Massimo 20 caratteri"),
  password: z.string().min(8, "Almeno 8 caratteri"),
});

type FormValues = z.infer<typeof schema>;

type Props = {
  email: string;
  onSubmit: (values: FormValues) => Promise<void> | void;
};

export function NewUserForm({ email, onSubmit }: Props) {
  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      username: "",
      password: "",
    },
  });

  return (
    <>
      <ThemedText style={styles.info}>
        L’email <ThemedText style={styles.bold}>{email}</ThemedText> non è
        registrata. Crea un account.
      </ThemedText>

      <ThemedText style={styles.label}>Username</ThemedText>
      <Controller
        control={control}
        name="username"
        render={({ field: { onChange, onBlur, value } }) => (
          <ThemedTextInput
            onBlur={onBlur}
            onChangeText={onChange}
            value={value}
            placeholder="Nome da usare nel gioco"
            error={!!errors.username}
          />
        )}
      />
      {errors.username && (
        <ThemedText style={styles.error}>{errors.username.message}</ThemedText>
      )}

      <ThemedText style={styles.label}>Password</ThemedText>
      <Controller
        control={control}
        name="password"
        render={({ field: { onChange, onBlur, value } }) => (
          <ThemedTextInput
            onBlur={onBlur}
            onChangeText={onChange}
            value={value}
            secureTextEntry
            autoCapitalize="none"
            placeholder="••••••••"
            error={!!errors.password}
          />
        )}
      />
      {errors.password && (
        <ThemedText style={styles.error}>{errors.password.message}</ThemedText>
      )}

      <ThemedButton
        title="Crea account"
        onPress={handleSubmit(onSubmit)}
        loading={isSubmitting}
      />
    </>
  );
}

const styles = StyleSheet.create({
  info: {
    marginBottom: 12,
  },
  bold: {
    fontWeight: "600",
  },
  label: {
    marginBottom: 4,
    fontSize: 14,
    fontWeight: "500",
  },
  error: {
    marginTop: 4,
    marginBottom: 8,
    fontSize: 12,
  },
});
