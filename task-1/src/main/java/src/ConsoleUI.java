package src;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

public class ConsoleUI {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);

    private final GameEngine engine;
    private final Scanner scanner;
    private final GameState state;

    public ConsoleUI(GameEngine engine) {
        this.engine = engine;
        this.state = engine.getGameState();
        this.scanner = new Scanner(System.in);
        logger.debug("ConsoleUI initialized.");
    }

    public void start() {
        logger.debug("Starting the application UI flow");
        printWelcomeMessage();
        printRules();
        gameLoop();
    }

    private void gameLoop() {
        logger.info("The game has started!");
        logger.debug("Game loop entered. Current attempts: {}", state.getAttempts());

        while (!state.isFinished()) {
            logger.info("Enter your guess: ");
            String guess = scanner.nextLine();

            logger.debug("User input received: '{}'", guess);

            ValidationResult validation = Validator.validate(guess);
            if (!validation.isValid()) {
                logger.debug("Input validation failed: '{}' -> {}", guess, validation.getErrorMessage());
                logger.info(validation.getErrorMessage());
                continue;
            }

            logger.debug("Input validated successfully.");
            GameState result = engine.processGuess(guess);

            logger.info("Result: {} bull(s), {} cow(s)", result.getBulls(), result.getCows());
            logger.debug("Current game state: Bulls={}, Cows={}, Attempts={}, Finished={}",
                    result.getBulls(), result.getCows(), result.getAttempts(), result.isFinished());

            if (result.isWin()) {
                logger.debug("Win condition met with guess: {}", guess);
                logger.info("You've guessed the number!");
                logger.info("Attempts: {}", result.getAttempts());
            }
        }

        askForNewGame();
    }

    private void askForNewGame() {
        logger.info("Would you like to play again? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        logger.debug("Play again response: '{}'", response);

        if (response.equals("yes") || response.equals("y")) {
            logger.debug("Restarting game session");
            restartGame();
        } else {
            logger.info("Thank you for playing Bulls and Cows!");
            logger.info("Goodbye!");
            logger.debug("Exiting game loop and finishing session");
        }
    }

    private void restartGame() {
        engine.restart();
        logger.debug("Engine restarted. Attempts reset to: {}", state.getAttempts());
        start();
    }

    private void printWelcomeMessage() {
        logger.info("========================================");
        logger.info("       WELCOME TO BULLS AND COWS       ");
        logger.info("========================================\n");
    }

    private void printRules() {
        logger.info("RULES:");
        logger.info("1. The computer has chosen a 4-digit secret number");
        logger.info("2. All digits in the number are unique (0-9)");
        logger.info("3. Your task is to guess this number");
        logger.info("4. After each guess, you'll get:");
        logger.info("   - BULLS: correct digit in correct position");
        logger.info("   - COWS: correct digit in wrong position");
    }
}