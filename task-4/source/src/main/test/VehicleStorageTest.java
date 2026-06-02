import factory.VehicleStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import product.*;

import static org.junit.jupiter.api.Assertions.*;

class VehicleStorageTest {

    private VehicleStorage vehicleStorage;

    @BeforeEach
    void setUp() {
        vehicleStorage = new VehicleStorage(5);
    }

    @Test
    void testPutAndGetCar() throws InterruptedException {

        Car car = createTestCar(1);

        vehicleStorage.put(car);

        assertEquals(1, vehicleStorage.getCurrentSize());
        assertEquals(1, vehicleStorage.getTotalProduced());

        Car extracted = vehicleStorage.get();

        assertNotNull(extracted);
        assertEquals(car.getId(), extracted.getId());

        assertEquals(0, vehicleStorage.getCurrentSize());
        assertEquals(1, vehicleStorage.getTotalProduced());
    }

    private Car createTestCar(long id) {
        return new Car(
                id,
                new Body(id),
                new Motor(id),
                new Accessory(id)
        );
    }
}