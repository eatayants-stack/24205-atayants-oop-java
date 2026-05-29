import factory.*;
import detail.Body;
import detail.Car;
import detail.Motor;
import detail.Accessory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AutoStorageTest {

    private AutoStorage autoStorage;

    @BeforeEach
    void setUp() {
        autoStorage = new AutoStorage(5);
    }

    @Test
    void testCheckoutNeededTasks() throws InterruptedException {
        // Initially empty -> need 5 tasks
        assertEquals(5, autoStorage.checkoutNeededTasks(), "First call: empty storage, should need 5");

        // Second call: tasksInFlight = 5, storage empty -> need 0
        assertEquals(0, autoStorage.checkoutNeededTasks(), "Second call: tasks already in flight, need 0");

        // Put one completed car into storage
        autoStorage.put(createTestCar(1));
        // Now storage size = 1, tasksInFlight = 5 -> total 6 > capacity -> need 0
        assertEquals(0, autoStorage.checkoutNeededTasks(), "After put: storage=1, inFlight=5 -> total 6, need 0");

        // One builder finishes -> releaseTask
        autoStorage.releaseTask();
        // storage=1, inFlight=4 -> total 5 == capacity -> need 0
        assertEquals(0, autoStorage.checkoutNeededTasks(), "After 1 release: storage=1, inFlight=4 -> total 5, need 0");

        // Second builder finishes -> releaseTask
        autoStorage.releaseTask();
        // storage=1, inFlight=3 -> total 4 < capacity -> need 1
        assertEquals(1, autoStorage.checkoutNeededTasks(), "After 2 releases: storage=1, inFlight=3 -> total 4, need 1");
    }

    private Car createTestCar(long id) {
        Body body = new Body(id);
        Motor motor = new Motor(id);
        Accessory accessory = new Accessory(id);
        return new Car(id, body, motor, accessory);
    }
}