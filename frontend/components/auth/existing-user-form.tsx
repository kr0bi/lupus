import { StyleSheet } from "react-native";
import { useForm, Controller } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { ThemedText } from "@/components/themed-text";
import { ThemedTextInput } from "@/components/themed-text-input";
import { ThemedButton } from "@/components/themed-button";

const schema = z.object({
  password: z.string().min(8, "Almeno 8 caratteri"),
});

type FormValues = z.infer<typeof schema>;

type Props = {
  email: string;
  onSubmit: (values: FormValues) => Promise<void> | void;
};

export function ExistingUserForm({ email, onSubmit }: Props) {
  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      password: "",
    },
  });

  return (
    <>
      <ThemedText style={styles.info}>
        L’email <ThemedText style={styles.bold}>{email}</ThemedText> è già
        registrata. Inserisci la password.
      </ThemedText>

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
        title="Accedi"
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
