import {
  Pressable,
  ActivityIndicator,
  StyleSheet,
  type PressableProps,
  type StyleProp,
  type ViewStyle,
} from "react-native";

import { useThemeColor } from "@/hooks/use-theme-color";
import { ThemedText } from "@/components/themed-text";

export type ThemedButtonProps = Omit<PressableProps, "children" | "style"> & {
  title: string;
  loading?: boolean;
  style?: StyleProp<ViewStyle>;
  lightColor?: string;
  darkColor?: string;
};

export function ThemedButton({
  title,
  loading,
  disabled,
  style,
  lightColor,
  darkColor,
  ...rest
}: ThemedButtonProps) {
  const tint = useThemeColor({ light: lightColor, dark: darkColor }, "tint");
  const textOnTint = useThemeColor({}, "background"); // good contrast on both themes

  const isDisabled = disabled || loading;

  return (
    <Pressable
      {...rest}
      disabled={isDisabled}
      style={({ pressed }) => [
        styles.button,
        {
          backgroundColor: tint,
          borderColor: tint,
          opacity: pressed || isDisabled ? 0.7 : 1,
        },
        style,
      ]}
    >
      {loading ? (
        <ActivityIndicator color={textOnTint} />
      ) : (
        <ThemedText style={[styles.text, { color: textOnTint }]}>
          {title}
        </ThemedText>
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    marginTop: 16,
    paddingVertical: 10,
    borderRadius: 6,
    borderWidth: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  text: {
    fontSize: 16,
    fontWeight: "600",
  },
});
