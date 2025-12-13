import { StyleSheet } from "react-native";
import { useForm, Controller } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { ThemedText } from "@/components/themed-text";
import { ThemedTextInput } from "@/components/themed-text-input";
import { ThemedButton } from "@/components/themed-button";

const schema = z.object({
  email: z.string().email("Email non valida"),
});

type FormValues = z.infer<typeof schema>;

type Props = {
  defaultEmail?: string;
  loading?: boolean;
  onSubmit: (values: FormValues) => Promise<void> | void;
};

export function EmailStepForm({ defaultEmail, loading, onSubmit }: Props) {
  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      email: defaultEmail ?? "",
    },
  });

  const isLoading = loading || isSubmitting;

  return (
    <>
      <ThemedText style={styles.label}>Email</ThemedText>
      <Controller
        control={control}
        name="email"
        render={({ field: { onChange, onBlur, value } }) => (
          <ThemedTextInput
            onBlur={onBlur}
            onChangeText={onChange}
            value={value}
            keyboardType="email-address"
            autoCapitalize="none"
            placeholder="tu@esempio.com"
            error={!!errors.email}
          />
        )}
      />
      {errors.email && (
        <ThemedText style={styles.error}>{errors.email.message}</ThemedText>
      )}

      <ThemedButton
        title="Continua"
        onPress={handleSubmit(onSubmit)}
        loading={isLoading}
      />
    </>
  );
}

const styles = StyleSheet.create({
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
