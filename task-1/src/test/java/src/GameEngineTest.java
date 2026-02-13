package src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {
    private GameEngine engine;
    private SecretNumber testSecret;

    @BeforeEach
    void setUp() {
        // Фиксированное секретное число для предсказуемых тестов
        testSecret = SecretNumber.of("1234");
        engine = new GameEngine(testSecret);
    }

    @Test
    void shouldStartGameNotFinished() {
        assertFalse(engine.isGameFinished());
        assertFalse(engine.isWin());
        assertEquals(0, engine.getAttempts());
    }

    @Test
    void shouldCountBullsAndCowsCorrectly() {
        // When
        GameEngine.ProcessResult result = engine.processGuess("1234");

        // Then
        assertEquals(4, result.getBulls());
        assertEquals(0, result.getCows());
        assertTrue(result.isWin());
        assertEquals(1, result.getAttempts());
    }

    @Test
    void shouldDetectOnlyBulls() {
        GameEngine.ProcessResult result = engine.processGuess("1234");
        assertEquals(4, result.getBulls());
        assertEquals(0, result.getCows());
    }

    @Test
    void shouldDetectOnlyCows() {
        // Перестановка всех цифр
        GameEngine.ProcessResult result = engine.processGuess("4321");

        assertEquals(0, result.getBulls());
        assertEquals(4, result.getCows());
        assertFalse(result.isWin());
    }

    @Test
    void shouldIncrementAttemptsWithEachGuess() {
        engine.processGuess("5678");
        assertEquals(1, engine.getAttempts());

        engine.processGuess("1234");
        assertEquals(2, engine.getAttempts());
    }

    @Test
    void shouldFinishGameAfterWin() {
        assertFalse(engine.isGameFinished());

        engine.processGuess("1234");

        assertTrue(engine.isGameFinished());
        assertTrue(engine.isWin());
    }

    @Test
    void shouldNotFinishGameAfterWrongGuess() {
        engine.processGuess("5678");

        assertFalse(engine.isGameFinished());
        assertFalse(engine.isWin());
    }

    @Test
    void shouldRestartGameWithNewSecret() {
        // Given
        String oldSecret = engine.getSecretValue();  // Сохраняем старое значение
        engine.processGuess("1234");  // Угадали
        assertTrue(engine.isGameFinished());

        // When
        engine.restart();

        // Then
        assertFalse(engine.isGameFinished());
        assertEquals(0, engine.getAttempts());
        assertNotEquals(oldSecret, engine.getSecretValue());
    }

    @Test
    void shouldHandleMultipleGuesses() {
        engine.processGuess("5678");  // 0 bulls, 0 cows
        engine.processGuess("4321");  // 0 bulls, 4 cows
        engine.processGuess("1234");  // 4 bulls, 0 cows

        assertEquals(3, engine.getAttempts());
        assertTrue(engine.isGameFinished());
    }

    // Дополнительные тесты

    @Test
    void shouldCalculateMixedBullsAndCows() {
        // Проверка комбинаций с частичным совпадением
        GameEngine.ProcessResult result = engine.processGuess("1243");
        assertResult(result, 2, 2, false); // 1,2 на месте (быки), 3,4 не на месте (коровы)
    }

    @Test
    void shouldNotCountBullAsCow() {
        // Убеждаемся, что бык не засчитывается как корова
        GameEngine.ProcessResult result = engine.processGuess("1235");
        assertResult(result, 3, 0, false); // 1,2,3 на месте, 5 отсутствует
    }

    @Test
    void shouldReturnCorrectStateAfterWin() {
        GameEngine.ProcessResult result = engine.processGuess("1234");
        assertTrue(result.isGameFinished());
        assertEquals(1, result.getAttempts());
        assertTrue(result.isWin());
        assertEquals(4, result.getBulls());
        assertEquals(0, result.getCows());
    }

    @Test
    void shouldIncrementAttemptsEvenAfterGameFinished() {
        // Проверяем поведение: даже после победы процесс-ход увеличивает счётчик
        engine.processGuess("1234");
        assertTrue(engine.isGameFinished());

        engine.processGuess("5678");
        assertEquals(2, engine.getAttempts()); // Попытка всё равно увеличивается
        assertTrue(engine.isGameFinished());    // Игра остаётся завершённой
        assertTrue(engine.isWin());             // Статус победы не меняется
    }

    @Test
    void restartAfterWinShouldResetGame() {
        engine.processGuess("1234");
        assertTrue(engine.isGameFinished());
        assertTrue(engine.isWin());

        engine.restart();

        assertFalse(engine.isGameFinished());
        assertFalse(engine.isWin());
        assertEquals(0, engine.getAttempts());
    }

    @Test
    void processResultShouldReflectGameStateAtMoment() {
        GameEngine.ProcessResult result1 = engine.processGuess("5678");
        assertEquals(0, result1.getBulls());
        assertEquals(0, result1.getCows());
        assertFalse(result1.isWin());
        assertFalse(result1.isGameFinished());
        assertEquals(1, result1.getAttempts());

        GameEngine.ProcessResult result2 = engine.processGuess("1234");
        assertEquals(4, result2.getBulls());
        assertEquals(0, result2.getCows());
        assertTrue(result2.isWin());
        assertTrue(result2.isGameFinished());
        assertEquals(2, result2.getAttempts());
    }

    @Test
    void getSecretValueShouldReturnCurrentSecret() {
        assertEquals("1234", engine.getSecretValue());

        engine.restart();
        assertNotEquals("1234", engine.getSecretValue());
    }

    private void assertResult(GameEngine.ProcessResult result,
                              int expectedBulls, int expectedCows, boolean expectedWin) {
        assertEquals(expectedBulls, result.getBulls());
        assertEquals(expectedCows, result.getCows());
        assertEquals(expectedWin, result.isWin());
    }
}