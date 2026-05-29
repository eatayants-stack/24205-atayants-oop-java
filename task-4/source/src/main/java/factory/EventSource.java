package factory;

public interface EventSource {
    void addObserver(Observer observer);
    void notifyObservers(Event event);
}