package src;

public class GameState {
    private int attempts;
    private boolean finished;
    private boolean won;
    private int cows;
    private int bulls;

    public GameState() {
        this.attempts = 0;
        this.finished = isFinished();
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
    public int getBulls() { return bulls; }
    public int getCows() { return cows; }
    public boolean isWin() { return won; }
    public void setBulls(int bulls) { this.bulls = bulls; }
    public void setCows(int cows) { this.cows = cows; }
}