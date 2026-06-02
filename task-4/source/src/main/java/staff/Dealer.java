package staff;

import factory.VehicleStorage;
import product.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dealer extends DelayService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Dealer.class);
    private final VehicleStorage carStorage;
    private final int dealerNumber;
    private final boolean logSale;

    public Dealer(VehicleStorage carStorage, int delayMs, int dealerNumber, boolean logSale) {
        super(delayMs);
        this.carStorage = carStorage;
        this.dealerNumber = dealerNumber;
        this.logSale = logSale;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Car car = carStorage.get();
                if (logSale) {
                    log.info("Dealer {} sold: {} (body {}, motor {}, accessory {})",
                            dealerNumber, car.getId(), car.getBodyId(),
                            car.getMotorId(), car.getAccessoryId());
                }
                Thread.sleep(getDelay());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}