package src;

import java.util.Scanner;

public class ConsoleUI {
    private final GameEngine engine;
    private final Scanner scanner;
    private final GameState state;

    public ConsoleUI(GameEngine engine) {
        this.engine = engine;
        this.state = engine.getGameState();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printWelcomeMessage();
        printRules();
        gameLoop();
    }

    private void gameLoop() {
        System.out.println("\nThe game has started!");

        while (!state.isFinished()) {
            System.out.print("Enter your guess: ");
            String guess = scanner.nextLine();


            ValidationResult validation = Validator.validate(guess);
            if (!validation.isValid()) {
                System.out.println(validation.getErrorMessage());
                continue;
            }

            GameState result = engine.processGuess(guess);


            System.out.println("Result: " + result.getBulls() + " bull(s), " + result.getCows() + " cow(s)");

            if (result.isWin()) {
                System.out.println("\nYou've guessed the number!");
                System.out.println("Attempts: " + result.getAttempts());
            }
        }

        askForNewGame();
    }

    private void askForNewGame() {
        System.out.print("\nWould you like to play again? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes") || response.equals("y")) {
            restartGame();
        } else {
            System.out.println("\nThank you for playing Bulls and Cows!");
            System.out.println("Goodbye!");
        }
    }

    private void restartGame() {
        engine.restart();
        start();
    }

    private void printWelcomeMessage() {
        System.out.println("========================================");
        System.out.println("       WELCOME TO BULLS AND COWS       ");
        System.out.println("========================================\n");
    }

    private void printRules() {
        System.out.println("RULES:");
        System.out.println("1. The computer has chosen a 4-digit secret number");
        System.out.println("2. All digits in the number are unique (0-9)");
        System.out.println("3. Your task is to guess this number");
        System.out.println("4. After each guess, you'll get:");
        System.out.println("   - BULLS: correct digit in correct position");
        System.out.println("   - COWS: correct digit in wrong position");
    }
}