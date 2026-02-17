package src;

public class GameEngine {
    private SecretNumber secretNumber;
    private final GameState state;

    public GameEngine(SecretNumber secretNumber) {
        this.secretNumber = secretNumber;
        this.state = new GameState();
    }

    public GameState processGuess(String guess) {

        state.incrementAttempts();

        int[] counts = calculateBullsAndCows(secretNumber.getValue(), guess);
        int bulls = counts[0];
        int cows = counts[1];

        state.setBulls(bulls);
        state.setCows(cows);

        boolean isWin = (bulls == 4);
        if (isWin) {
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
        this.secretNumber = SecretNumber.GenerateSecret();
        this.state.reset();
    }
    public GameState getGameState() {
        return state;
    }
}
