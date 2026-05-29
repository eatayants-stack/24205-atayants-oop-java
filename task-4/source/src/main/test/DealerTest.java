import factory.AutoStorage;
import detail.Car;
import detail.Body;
import detail.Motor;
import detail.Accessory;
import staff.Dealer;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    @Test
    void testDealerTakesCar() throws InterruptedException {
        AutoStorage autoStorage = new AutoStorage(5);
        // Pre‑fill storage with one car
        Car car = new Car(1, new Body(1), new Motor(1), new Accessory(1));
        autoStorage.put(car);

        AtomicBoolean carTaken = new AtomicBoolean(false);
        Dealer dealer = new Dealer(autoStorage, 10, 1, false);
        Thread t = new Thread(() -> {
            dealer.run();
            carTaken.set(true);
        });
        t.start();
        Thread.sleep(150); // longer than delay
        t.interrupt();

        // Check that dealer took the car (storage now empty)
        assertEquals(0, autoStorage.getCurrentSize());
    }
}