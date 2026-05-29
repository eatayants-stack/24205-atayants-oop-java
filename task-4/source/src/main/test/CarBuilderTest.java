import staff.*;
import detail.*;
import factory.AutoStorage;
import factory.Storage;
import factory.FactoryStat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarBuilderTest {

    @Test
    void testCarBuilderAssemblesCar() throws InterruptedException {
        Storage<Body> bodyStorage = new Storage<>(10);
        Storage<Motor> motorStorage = new Storage<>(10);
        Storage<Accessory> accessoryStorage = new Storage<>(10);
        AutoStorage autoStorage = new AutoStorage(10);
        FactoryStat stat = new FactoryStat();

        // Pre‑fill component storages
        Body body = new Body(1);
        Motor motor = new Motor(2);
        Accessory accessory = new Accessory(3);
        bodyStorage.put(body);
        motorStorage.put(motor);
        accessoryStorage.put(accessory);

        // Run builder
        CarBuilder builder = new CarBuilder(bodyStorage, motorStorage, accessoryStorage, autoStorage, stat);
        builder.run();

        Car assembled = autoStorage.dealerTake();
        assertNotNull(assembled);
        assertEquals(body.getId(), assembled.getBody());
        assertEquals(motor.getId(), assembled.getMotor());
        assertEquals(accessory.getId(), assembled.getAccessory());

        assertEquals(1, stat.getTotalCarsProduced());
        assertEquals(10, autoStorage.checkoutNeededTasks());
    }
}