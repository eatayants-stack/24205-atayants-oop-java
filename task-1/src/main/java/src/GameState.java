package src;

public class GameState {
    private int attempts;
    private boolean finished;
    private boolean won;

    public GameState() {
        this.attempts = 0;
        this.finished = false;
        this.won = false;
    }

    public void incrementAttempts() {
        attempts++;
    }

    public void finish(boolean isWin) {
        this.finished = true;
        this.won = isWin;
    }

    public void reset() {
        attempts = 0;
        finished = false;
        won = false;
    }

    public int getAttempts() { return attempts; }
    public boolean isFinished() { return finished; }
    public boolean isWon() { return won; }
}