package src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {
    private GameEngine engine;
    private SecretNumber testSecret;
    private GameState state;

    @BeforeEach
    void setUp() {
        // Фиксированное секретное число для предсказуемых тестов
        testSecret = SecretNumber.of("1234");
        engine = new GameEngine(testSecret);
        state = engine.getGameState();
    }

    @Test
    void shouldStartGameNotFinished() {
        assertFalse(state.isFinished());
        assertFalse(state.isWin());
        assertEquals(0, state.getAttempts());
    }

    @Test
    void shouldCountBullsAndCowsCorrectly() {
        // When
        GameState result = engine.processGuess("1234");

        // Then
        assertEquals(4, result.getBulls());
        assertEquals(0, result.getCows());
        assertTrue(result.isWin());
        assertEquals(1, result.getAttempts());
    }

    @Test
    void shouldDetectOnlyBulls() {
        GameState result = engine.processGuess("1234");
        assertEquals(4, result.getBulls());
        assertEquals(0, result.getCows());
    }

    @Test
    void shouldDetectOnlyCows() {
        // Перестановка всех цифр
        GameState result = engine.processGuess("4321");

        assertEquals(0, result.getBulls());
        assertEquals(4, result.getCows());
        assertFalse(result.isWin());
    }

    @Test
    void shouldIncrementAttemptsWithEachGuess() {
        engine.processGuess("5678");
        assertEquals(1, state.getAttempts());

        engine.processGuess("1234");
        assertEquals(2, state.getAttempts());
    }

    @Test
    void shouldFinishGameAfterWin() {
        assertFalse(state.isFinished());

        engine.processGuess("1234");

        assertTrue(state.isFinished());
        assertTrue(state.isWin());
    }

    @Test
    void shouldNotFinishGameAfterWrongGuess() {
        engine.processGuess("5678");

        assertFalse(state.isFinished());
        assertFalse(state.isWin());
    }


    @Test
    void shouldHandleMultipleGuesses() {
        engine.processGuess("5678");  // 0 bulls, 0 cows
        engine.processGuess("4321");  // 0 bulls, 4 cows
        engine.processGuess("1234");  // 4 bulls, 0 cows

        assertEquals(3, state.getAttempts());
        assertTrue(state.isFinished());
    }

    // Дополнительные тесты

    @Test
    void shouldCalculateMixedBullsAndCows() {
        // Проверка комбинаций с частичным совпадением
        GameState result = engine.processGuess("1243");
        assertResult(result, 2, 2, false); // 1,2 на месте (быки), 3,4 не на месте (коровы)
    }

    @Test
    void shouldNotCountBullAsCow() {
        // Убеждаемся, что бык не засчитывается как корова
        GameState result = engine.processGuess("1235");
        assertResult(result, 3, 0, false); // 1,2,3 на месте, 5 отсутствует
    }

    @Test
    void shouldReturnCorrectStateAfterWin() {
        GameState result = engine.processGuess("1234");
        assertTrue(result.isFinished());
        assertEquals(1, result.getAttempts());
        assertTrue(result.isWin());
        assertEquals(4, result.getBulls());
        assertEquals(0, result.getCows());
    }

    @Test
    void shouldIncrementAttemptsEvenAfterGameFinished() {
        // Проверяем поведение: даже после победы процесс-ход увеличивает счётчик
        engine.processGuess("1234");
        assertTrue(state.isFinished());

        engine.processGuess("5678");
        assertEquals(2, state.getAttempts()); // Попытка всё равно увеличивается
        assertTrue(state.isFinished());    // Игра остаётся завершённой
        assertTrue(state.isWin());             // Статус победы не меняется
    }

    @Test
    void restartAfterWinShouldResetGame() {
        engine.processGuess("1234");
        assertTrue(state.isFinished());
        assertTrue(state.isWin());

        engine.restart();

        assertFalse(state.isFinished());
        assertFalse(state.isWin());
        assertEquals(0, state.getAttempts());
    }

    @Test
    void processResultShouldReflectGameStateAtMoment() {
        GameState result1 = engine.processGuess("5678");
        assertEquals(0, result1.getBulls());
        assertEquals(0, result1.getCows());
        assertFalse(result1.isWin());
        assertFalse(result1.isFinished());
        assertEquals(1, result1.getAttempts());

        GameState result2 = engine.processGuess("1234");
        assertEquals(4, result2.getBulls());
        assertEquals(0, result2.getCows());
        assertTrue(result2.isWin());
        assertTrue(result2.isFinished());
        assertEquals(2, result2.getAttempts());
    }


    private void assertResult(GameState result,
                              int expectedBulls, int expectedCows, boolean expectedWin) {
        assertEquals(expectedBulls, result.getBulls());
        assertEquals(expectedCows, result.getCows());
        assertEquals(expectedWin, result.isWin());
    }
}