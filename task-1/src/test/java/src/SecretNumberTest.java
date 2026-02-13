package src;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

public class SecretNumberTest {

    @Test
    void shouldCreateRandomSecretNumber() {
        // When
        SecretNumber secret = SecretNumber.GenerateSecret();

        // Then
        assertNotNull(secret);
        assertNotNull(secret.getValue());
        assertEquals(4, secret.getValue().length());
    }

    @RepeatedTest(10)  // Проверим 10 случайных чисел
    void shouldHaveOnlyDigitsInRandomSecret() {
        SecretNumber secret = SecretNumber.GenerateSecret();
        String value = secret.getValue();

        for (int i = 0; i < value.length(); i++) {
            assertTrue(Character.isDigit(value.charAt(i)));
        }
    }

    @RepeatedTest(10)
    void shouldHaveUniqueDigitsInRandomSecret() {
        SecretNumber secret = SecretNumber.GenerateSecret();
        String value = secret.getValue();

        for (int i = 0; i < value.length(); i++) {
            for (int j = i + 1; j < value.length(); j++) {
                assertNotEquals(value.charAt(i), value.charAt(j));
            }
        }
    }

    @Test
    void shouldCreateSecretWithGivenValue() {
        // Given
        String expected = "1234";

        // When
        SecretNumber secret = SecretNumber.of(expected);

        // Then
        assertEquals(expected, secret.getValue());
    }
}