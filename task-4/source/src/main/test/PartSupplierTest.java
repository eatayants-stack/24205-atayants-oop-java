import factory.Storage;
import org.junit.jupiter.api.Test;
import product.Body;
import product.Motor;
import staff.PartSupplier;

import static org.junit.jupiter.api.Assertions.*;

class PartSupplierTest {

    @Test
    void testSupplierProducesAndPutsBody() throws Exception {

        Storage<Body> storage = new Storage<>(10);

        PartSupplier<Body> supplier =
                new PartSupplier<>(
                        storage,
                        () -> new Body(1),
                        10
                );

        Thread thread = new Thread(supplier);

        thread.start();

        Thread.sleep(150);

        thread.interrupt();
        thread.join(1000);

        assertTrue(storage.getCurrentSize() > 0);

        Body body = storage.get();

        assertNotNull(body);
    }

    @Test
    void testSetDelayChangesBehavior() throws Exception {

        Storage<Motor> storage = new Storage<>(10);

        PartSupplier<Motor> supplier =
                new PartSupplier<>(
                        storage,
                        () -> new Motor(1),
                        1000
                );

        supplier.setDelay(10);

        Thread thread = new Thread(supplier);

        thread.start();

        Thread.sleep(150);

        thread.interrupt();
        thread.join(1000);

        assertTrue(storage.getCurrentSize() > 0);

        Motor motor = storage.get();

        assertNotNull(motor);
    }
}