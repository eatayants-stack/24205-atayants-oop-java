import staff.*;
import factory.Storage;
import detail.Body;
import detail.Motor;
import utilities.DetailType;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SupplierTest {

    @Test
    void testSupplierProducesAndPutsBody() throws Exception {
        Storage<Body> storage = new Storage<>(10);
        Supplier supplier = new Supplier(storage, DetailType.BODY, 10);

        Thread t = new Thread(supplier);
        t.start();
        Thread.sleep(150);
        t.interrupt();

        assertTrue(storage.getCurrentSize() > 0);
        Body body = storage.take();
        assertNotNull(body);
    }

    @Test
    void testSetDelayChangesBehavior() throws Exception {
        Storage<Motor> storage = new Storage<>(10);
        Supplier supplier = new Supplier(storage, DetailType.MOTOR, 1000);
        supplier.setDelay(10);

        Thread t = new Thread(supplier);
        t.start();
        Thread.sleep(150);
        t.interrupt();

        assertTrue(storage.getCurrentSize() > 0);
        Motor motor = storage.take();
        assertNotNull(motor);
    }
}