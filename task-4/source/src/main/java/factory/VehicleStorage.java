package factory;

import product.Car;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class VehicleStorage extends Storage<Car> implements Observable {
    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    public VehicleStorage(int capacity) {
        super(capacity);
    }

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        for (Observer o : observers) {
            o.update(event);
        }
    }

    @Override
    public Car get() throws InterruptedException {
        Car car = super.get();
        notifyObservers(Event.CAR_DEMAND);
        return car;
    }
}