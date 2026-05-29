package staff;

import detail.*;
import factory.AutoStorage;
import factory.Storage;
import factory.FactoryStat;
import utilities.IDGenerator;

public class CarBuilder implements Runnable {
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final AutoStorage autoStorage;
    private final FactoryStat stat;

    public CarBuilder(Storage<Body> bodyStorage, Storage<Motor> motorStorage,
                      Storage<Accessory> accessoryStorage, AutoStorage autoStorage,
                      FactoryStat stat) {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;
        this.stat = stat;
    }

    @Override
    public void run() {
        try {
            Body body = bodyStorage.take();
            Motor motor = motorStorage.take();
            Accessory accessory = accessoryStorage.take();
            long carId = IDGenerator.INSTANCE.nextCarId();
            Car car = new Car(carId, body, motor, accessory);
            autoStorage.put(car);
            stat.incrementCarsProduced();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            autoStorage.releaseTask();
        }
    }
}