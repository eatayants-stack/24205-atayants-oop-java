package factory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventSourceStorage<T> extends Storage<T> implements EventSource {
    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    public EventSourceStorage(int capacity) {
        super(capacity);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(Event event) {
        for (Observer obs : observers) {
            obs.update(event);
        }
    }

    @Override
    public void put(T item) throws InterruptedException {
        super.put(item);
        notifyObservers(new Event("STORAGE_CHANGED", this));
    }

    @Override
    public T take() throws InterruptedException {
        T item = super.take();
        notifyObservers(new Event("STORAGE_CHANGED", this));
        return item;
    }
}