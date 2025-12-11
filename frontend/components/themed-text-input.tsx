import { TextInput, StyleSheet, type TextInputProps } from "react-native";

import { useThemeColor } from "@/hooks/use-theme-color";

export type ThemedTextInputProps = TextInputProps & {
  lightColor?: string;
  darkColor?: string;
  error?: boolean;
};

export function ThemedTextInput({
  style,
  lightColor,
  darkColor,
  error,
  ...rest
}: ThemedTextInputProps) {
  const backgroundColor = useThemeColor(
    { light: lightColor, dark: darkColor },
    "background",
  );
  const textColor = useThemeColor({}, "text");
  const baseBorderColor = useThemeColor({}, "icon");
  const placeholderColor = useThemeColor({}, "icon");

  const borderColor = error ? "red" : baseBorderColor;

  return (
    <TextInput
      style={[
        styles.input,
        { backgroundColor, color: textColor, borderColor },
        style,
      ]}
      placeholderTextColor={placeholderColor}
      {...rest}
    />
  );
}

const styles = StyleSheet.create({
  input: {
    borderWidth: 1,
    borderRadius: 6,
    paddingHorizontal: 10,
    paddingVertical: 8,
    fontSize: 16,
  },
});
