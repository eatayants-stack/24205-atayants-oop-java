package factory;

import detail.Car;

public class AutoStorage {
    private final EventSourceStorage<Car> storage;
    private boolean needMoreCars = false;
    private int tasksInFlight = 0;

    public AutoStorage(int capacity) {
        this.storage = new EventSourceStorage<>(capacity);
    }

    public Car dealerTake() throws InterruptedException {
        Car car = storage.take();
        synchronized (this) {
            needMoreCars = true;
            notifyAll();
        }
        return car;
    }

    public void put(Car car) throws InterruptedException {
        storage.put(car);
    }

    public synchronized void releaseTask() {
        if (tasksInFlight > 0) {
            tasksInFlight--;
        }
    }

    public synchronized void waitForControllerNotification() throws InterruptedException {
        while (!needMoreCars) wait();
        needMoreCars = false;
    }

    public synchronized void notifyController() {
        needMoreCars = true;
        notifyAll();
    }

    public synchronized int checkoutNeededTasks() {
        int currentTotal = storage.getCurrentSize() + tasksInFlight;
        int needed = storage.getCapacity() - currentTotal;
        if (needed > 0) {
            tasksInFlight += needed;
            return needed;
        }
        return 0;
    }

    public void addObserver(Observer observer) {
        storage.addObserver(observer);
    }

    public int getCurrentSize() {
        return storage.getCurrentSize();
    }
}