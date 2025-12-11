import { z } from "zod";

export const registrationSchema = z.object({
  email: z.email("Invalid email"),
  password: z.string().min(8, "At least 8 characters"),
  username: z
    .string()
    .min(2, "At least 2 characters")
    .max(20, "Max 20 characters"),
});

export type RegistrationFormValues = z.infer<typeof registrationSchema>;
