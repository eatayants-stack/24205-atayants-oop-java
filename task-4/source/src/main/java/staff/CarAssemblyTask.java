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

    @Override
    public void execute() {
        try {
            Body body = bodyStorage.get();
            Motor motor = motorStorage.get();
            Accessory accessory = accessoryStorage.get();
            long carId = IDGenerator.INSTANCE.nextCarId();
            Car car = new Car(carId, body, motor, accessory);
            carStorage.put(car);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}