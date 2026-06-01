import factory.Storage;
import org.junit.jupiter.api.Test;
import product.Body;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @Test
    void testPutAndGet() throws InterruptedException {

        Storage<Body> storage = new Storage<>(2);

        Body body1 = new Body(1);
        Body body2 = new Body(2);

        storage.put(body1);
        storage.put(body2);

        assertEquals(2, storage.getCurrentSize());

        Body item = storage.get();

        assertEquals(body1.getId(), item.getId());
        assertEquals(1, storage.getCurrentSize());
    }

    @Test
    void testBlockingGet() throws Exception {

        Storage<Body> storage = new Storage<>(1);

        Thread getter = new Thread(() -> {
            try {
                storage.get();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        getter.start();

        Thread.sleep(100);

        assertTrue(getter.isAlive());

        storage.put(new Body(1));

        getter.join(1000);

        assertFalse(getter.isAlive());
    }

    @Test
    void testBlockingPut() throws Exception {

        Storage<Body> storage = new Storage<>(1);

        storage.put(new Body(1));

        Thread putter = new Thread(() -> {
            try {
                storage.put(new Body(2));
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        putter.start();

        Thread.sleep(100);

        assertTrue(putter.isAlive());

        storage.get();

        putter.join(1000);

        assertFalse(putter.isAlive());
    }

    @Test
    void testCapacity() {

        Storage<Body> storage = new Storage<>(5);

        assertEquals(5, storage.getCapacity());
    }

    @Test
    void testTotalProducedCounter() throws InterruptedException {

        Storage<Body> storage = new Storage<>(10);

        storage.put(new Body(1));
        storage.put(new Body(2));
        storage.put(new Body(3));

        assertEquals(3, storage.getTotalProduced());
    }
}