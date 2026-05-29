package staff;

import detail.*;
import factory.Storage;
import utilities.IDGenerator;
import utilities.DetailType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Supplier implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Supplier.class);
    private final Storage<Detail> storage;
    private final DetailType type;
    private volatile int delayMs;

    @SuppressWarnings("unchecked")
    public Supplier(Storage<? extends Detail> storage, DetailType type, int delayMs) {
        // Safe because we only put parts of the correct type (Body, Motor, or Accessory)
        this.storage = (Storage<Detail>) storage;
        this.type = type;
        this.delayMs = delayMs;
    }

    public void setDelay(int delayMs) {
        this.delayMs = delayMs;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(delayMs);
                Detail part = createPart();
                storage.put(part);
                logger.debug("{} produced", part.getClass().getSimpleName());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Detail createPart() {
        long id;
        switch (type) {
            case BODY:
                id = IDGenerator.INSTANCE.nextBodyId();
                return new Body(id);
            case MOTOR:
                id = IDGenerator.INSTANCE.nextMotorId();
                return new Motor(id);
            case ACCESSORY:
                id = IDGenerator.INSTANCE.nextAccessoryId();
                return new Accessory(id);
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }
}