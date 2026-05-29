package factory;

public class Event {
    private final String type;
    public Event(String type, Object data) {
        this.type = type;
    }
    public String getType() { return type; }
}