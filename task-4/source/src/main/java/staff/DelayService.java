package staff;


public abstract class DelayService {
    private volatile int delayMs;

    public DelayService(int delayMs) {
        this.delayMs = delayMs;
    }

    public void setDelay(int delayMs) {
        if (delayMs >= 0) {
            this.delayMs = delayMs;
        }
    }

    public int getDelay() {
        return delayMs;
    }
}