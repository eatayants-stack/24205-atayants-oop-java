package src;

public class Validator {
    public static ValidationResult validate(String str) {
        if (str == null) {
            return ValidationResult.invalid("Empty input");
        }
        if (str.length() != 4) {
            return ValidationResult.invalid("Incorrect amount of input (need 4 digits)");
        }
        for (int i = 0; i < 4; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return ValidationResult.invalid("Incorrect input: only digits allowed");
            }
        }
        for (int j = 0; j < str.length(); j++) {
            for (int k = j + 1; k < str.length(); k++) {
                if (str.charAt(j) == str.charAt(k)) {
                    return ValidationResult.invalid("Digits must be unique");
                }
            }
        }
        return ValidationResult.valid();
    }
}