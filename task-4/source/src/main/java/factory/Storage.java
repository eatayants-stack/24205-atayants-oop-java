package factory;

import java.util.ArrayDeque;
import java.util.Deque;

public class Storage<T> {
    protected final int capacity;
    protected final Deque<T> items = new ArrayDeque<>();

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (items.size() >= capacity) wait();
        items.add(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (items.isEmpty()) wait();
        T item = items.pop();
        notifyAll();
        return item;
    }

    public synchronized int getCurrentSize() { return items.size(); }
    public int getCapacity() { return capacity; }
}