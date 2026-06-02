package staff;

import factory.Storage;
import product.*;
import utilities.IDGenerator;

public class CarAssemblyTask implements WorkTask {
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Car> carStorage;

    public CarAssemblyTask(Storage<Body> bodyStorage,
                           Storage<Motor> motorStorage,
                           Storage<Accessory> accessoryStorage,
                           Storage<Car> carStorage) {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
    }

    private <T extends Product> void put(Storage<T> storage, T item) {
        if (item == null) return;
        boolean interrupted = false;
        while (true) {
            try {
                storage.put(item);
                break;
            } catch (InterruptedException ex) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void execute() {
        Body body = null;
        Motor motor = null;
        Accessory accessory = null;
        try {
            body = bodyStorage.get();
            motor = motorStorage.get();
            accessory = accessoryStorage.get();
            long carId = IDGenerator.INSTANCE.nextCarId();
            Car car = new Car(carId, body, motor, accessory);
            put(carStorage, car);
        } catch (InterruptedException e) {
            put(bodyStorage, body);
            put(motorStorage, motor);
            put(accessoryStorage, accessory);
            Thread.currentThread().interrupt();
        }
    }
}