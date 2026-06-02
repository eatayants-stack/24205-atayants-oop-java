import factory.Storage;
import factory.VehicleStorage;
import org.junit.jupiter.api.Test;
import product.*;
import staff.CarAssemblyTask;

import static org.junit.jupiter.api.Assertions.*;

class CarAssemblyTaskTest {

    @Test
    void testCarAssemblyTaskAssemblesCar() throws InterruptedException {

        Storage<Body> bodyStorage = new Storage<>(10);
        Storage<Motor> motorStorage = new Storage<>(10);
        Storage<Accessory> accessoryStorage = new Storage<>(10);
        VehicleStorage carStorage = new VehicleStorage(10);

        Body body = new Body(1);
        Motor motor = new Motor(2);
        Accessory accessory = new Accessory(3);

        bodyStorage.put(body);
        motorStorage.put(motor);
        accessoryStorage.put(accessory);

        CarAssemblyTask task = new CarAssemblyTask(
                bodyStorage,
                motorStorage,
                accessoryStorage,
                carStorage
        );

        task.execute();

        Car assembled = carStorage.get();

        assertNotNull(assembled);

        assertEquals(body.getId(), assembled.getBodyId());
        assertEquals(motor.getId(), assembled.getMotorId());
        assertEquals(accessory.getId(), assembled.getAccessoryId());

        assertEquals(1, carStorage.getCurrentSize() + 1);
        assertEquals(1, carStorage.getTotalProduced());
    }
}