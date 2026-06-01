package staff;

import factory.Storage;
import product.*;
import threadpool.ThreadPool;

public class AssemblyLine {
    private final ThreadPool threadPool;
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Car> carStorage;

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
        threadPool.addTask(new CarAssemblyTask(bodyStorage, motorStorage, accessoryStorage, carStorage));
    }

    public int getPendingOrders() {
        return threadPool.getTaskQueueSize();
    }

    public int getWorkersCount() {
        return threadPool.getThreadCount();
    }

    public void start() {
        threadPool.start();
    }

    public void stop() {
        threadPool.stop();
    }
}