import factory.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @Test
    void testPutAndTake() throws InterruptedException {
        Storage<String> storage = new Storage<>(2);
        storage.put("a");
        storage.put("b");
        assertEquals(2, storage.getCurrentSize());

        String item = storage.take();
        assertEquals("a", item);
        assertEquals(1, storage.getCurrentSize());
    }

    @Test
    void testBlockingTake() throws InterruptedException {
        Storage<String> storage = new Storage<>(1);
        Thread taker = new Thread(() -> {
            try {
                storage.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        taker.start();
        Thread.sleep(100);
        assertTrue(taker.isAlive());
        storage.put("x");
        Thread.sleep(100);
        assertFalse(taker.isAlive());
    }

    @Test
    void testBlockingPut() throws InterruptedException {
        Storage<String> storage = new Storage<>(1);
        storage.put("a");
        Thread putter = new Thread(() -> {
            try {
                storage.put("b");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        putter.start();
        Thread.sleep(100);
        assertTrue(putter.isAlive());
        storage.take();
        Thread.sleep(100);
        assertFalse(putter.isAlive());
    }

    @Test
    void testCapacity() {
        Storage<String> storage = new Storage<>(5);
        assertEquals(5, storage.getCapacity());
    }
}