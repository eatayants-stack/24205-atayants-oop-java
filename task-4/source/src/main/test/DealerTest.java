import factory.VehicleStorage;
import org.junit.jupiter.api.Test;
import product.*;
import staff.Dealer;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    @Test
    void testDealerConsumesCarAndStopsCorrectly() throws Exception {

        VehicleStorage vehicleStorage = new VehicleStorage(5);

        vehicleStorage.put(
                new Car(
                        1,
                        new Body(1),
                        new Motor(1),
                        new Accessory(1)
                )
        );

        Dealer dealer =
                new Dealer(vehicleStorage, 10, 1, false);

        Thread dealerThread = new Thread(dealer);

        dealerThread.start();

        Thread.sleep(100);

        assertEquals(0, vehicleStorage.getCurrentSize());

        dealerThread.interrupt();
        dealerThread.join(1000);

        assertFalse(dealerThread.isAlive());
    }
}