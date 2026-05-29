package staff;

import detail.Car;
import factory.AutoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dealer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Dealer.class);
    private final AutoStorage autoStorage;
    private final int delayMs;
    private final int dealerNumber;
    private final boolean logEnabled;

    public Dealer(AutoStorage autoStorage, int delayMs, int dealerNumber, boolean logEnabled) {
        this.autoStorage = autoStorage;
        this.delayMs = delayMs;
        this.dealerNumber = dealerNumber;
        this.logEnabled = logEnabled;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(delayMs);
                Car car = autoStorage.dealerTake();
                if (logEnabled) {
                    logger.info("Dealer {}: Auto {} (Body: {}, Motor: {}, Accessory: {})",
                            dealerNumber, car.getId(), car.getBody(), car.getMotor(), car.getAccessory());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}