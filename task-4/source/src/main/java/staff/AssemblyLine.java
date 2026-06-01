package staff;

import factory.Storage;
import product.*;
import threadpool.ThreadPool;
import java.util.concurrent.atomic.AtomicInteger;

public class AssemblyLine {
    private final ThreadPool threadPool;
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Car> carStorage;

    private final AtomicInteger activeOrdersCount = new AtomicInteger(0);

    public AssemblyLine(int workers, int queueSize,
                        Storage<Body> bodyStorage, Storage<Motor> motorStorage,
                        Storage<Accessory> accessoryStorage, Storage<Car> carStorage) {
        this.threadPool = new ThreadPool(workers, queueSize);
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
    }

    public void assembleCar() {
        activeOrdersCount.incrementAndGet();
        threadPool.addTask(() -> {
            try {
                new CarAssemblyTask(bodyStorage, motorStorage, accessoryStorage, carStorage).execute();
            } finally {
                activeOrdersCount.decrementAndGet();
            }
        });
    }

    public int getPendingOrders() {
        return activeOrdersCount.get();
    }
    public void start() {
        threadPool.start();
    }
    public void stop() {
        threadPool.stop();
    }
}