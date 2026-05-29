package factory;

import java.util.concurrent.atomic.AtomicInteger;

public class FactoryStat {
    private final AtomicInteger totalCarsProduced = new AtomicInteger(0);

    public void incrementCarsProduced() {
        totalCarsProduced.incrementAndGet();
    }

    public int getTotalCarsProduced() {
        return totalCarsProduced.get();
    }
}