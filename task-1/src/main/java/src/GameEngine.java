package src;

public class GameEngine {
    private SecretNumber secretNumber;
    private final GameState state;

    public GameEngine(SecretNumber secretNumber) {
        this.secretNumber = secretNumber;
        this.state = new GameState();
    }

    public ProcessResult processGuess(String guess) {
        state.incrementAttempts();

        int[] counts = calculateBullsAndCows(secretNumber.getValue(), guess);
        int bulls = counts[0];
        int cows = counts[1];

        boolean isWin = (bulls == 4);
        if (isWin) {
            state.finish(true);
        }

        return new ProcessResult(bulls, cows, isWin, state);
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



    public boolean isGameFinished() {
        return state.isFinished();
    }

    public boolean isWin() {
        return state.isWon();
    }

    public int getAttempts() {
        return state.getAttempts();
    }

    public String getSecretValue() {
        return secretNumber.getValue();
    }


    public static class ProcessResult {
        private final int bulls;
        private final int cows;
        private final boolean win;
        private final int attempts;
        private final boolean gameFinished;

        public ProcessResult(int bulls, int cows, boolean win, GameState state) {
            this.bulls = bulls;
            this.cows = cows;
            this.win = win;
            this.attempts = state.getAttempts();
            this.gameFinished = state.isFinished();
        }

        public int getBulls() { return bulls; }
        public int getCows() { return cows; }
        public boolean isWin() { return win; }
        public int getAttempts() { return attempts; }
        public boolean isGameFinished() { return gameFinished; }
    }
}