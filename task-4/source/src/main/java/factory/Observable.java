package factory;

public interface Observable {
    void register(Observer observer);
    void notifyObservers(Event event);
}