package utilities;

import java.util.concurrent.atomic.AtomicLong;

public enum IDGenerator {
    INSTANCE;
    private final AtomicLong bodyId = new AtomicLong(1);
    private final AtomicLong motorId = new AtomicLong(1);
    private final AtomicLong accessoryId = new AtomicLong(1);
    private final AtomicLong carId = new AtomicLong(1);

    public long nextBodyId() { return bodyId.getAndIncrement(); }
    public long nextMotorId() { return motorId.getAndIncrement(); }
    public long nextAccessoryId() { return accessoryId.getAndIncrement(); }
    public long nextCarId() { return carId.getAndIncrement(); }
}