package src;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);

    private SecretNumber secretNumber;
    private final GameState state;

    public GameEngine(SecretNumber secretNumber) {
        this.secretNumber = secretNumber;
        this.state = new GameState();
        logger.debug("GameEngine initialized. Secret number for this session: {}", secretNumber.getValue());
    }

    public GameState processGuess(String guess) {
        state.incrementAttempts();
        logger.debug("Processing guess #{} : '{}'", state.getAttempts(), guess);

        int[] counts = calculateBullsAndCows(secretNumber.getValue(), guess);
        int bulls = counts[0];
        int cows = counts[1];

        state.setBulls(bulls);
        state.setCows(cows);

        logger.debug("Calculation results for '{}': Bulls={}, Cows={}", guess, bulls, cows);

        if (bulls == 4) {
            logger.info("Win condition reached! Guess '{}' matches secret '{}'", guess, secretNumber.getValue());
            state.finish(true);
        }

        return state;
    }

    private int[] calculateBullsAndCows(String secret, String guess) {
        int bulls = 0;
        int cows = 0;

        for (int i = 0; i < 4; i++) {
            char guessChar = guess.charAt(i);
            char secretChar = secret.charAt(i);

            if (guessChar == secretChar) {
                bulls++;
            } else if (secret.indexOf(guessChar) != -1) {
                cows++;
            }
        }

        return new int[]{bulls, cows};
    }

    public void restart() {
        logger.debug("Restarting game. Previous secret was: {}", secretNumber.getValue());
        this.secretNumber = SecretNumber.GenerateSecret();
        this.state.reset();
        logger.info("Game restarted with a new secret number.");
        logger.debug("New secret number: {}", secretNumber.getValue());
    }

    public GameState getGameState() {
        return state;
    }
}