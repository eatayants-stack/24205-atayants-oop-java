package src;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.EmptySource;
import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    @Test
    void shouldAcceptValidGuess() {
        // Given
        String validGuess = "1234";

        // When
        ValidationResult result = Validator.validate(validGuess);

        // Then
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @ParameterizedTest
    @NullSource
    void shouldRejectNull(String guess) {
        ValidationResult result = Validator.validate(guess);
        assertFalse(result.isValid());
        assertEquals("Empty input", result.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "12345", "12", "1"})
    void shouldRejectWrongLength(String guess) {
        ValidationResult result = Validator.validate(guess);
        assertFalse(result.isValid());
        assertEquals("Incorrect amount of input (need 4 digits)", result.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12a4", "abcd", "12 4", "12.4", "1b3d"})
    void shouldRejectNonDigitCharacters(String guess) {
        ValidationResult result = Validator.validate(guess);
        assertFalse(result.isValid());
        assertEquals("Incorrect input: only digits allowed", result.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1123", "1223", "1231", "1111", "1233"})
    void shouldRejectNonUniqueDigits(String guess) {
        ValidationResult result = Validator.validate(guess);
        assertFalse(result.isValid());
        assertEquals("Digits must be unique", result.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "5678", "9012", "9876", "1023"})
    void shouldAcceptAllValidGuesses(String guess) {
        ValidationResult result = Validator.validate(guess);
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
}