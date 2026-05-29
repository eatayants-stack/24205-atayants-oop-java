import threadpool.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {

    @Test
    void testExecuteAndQueueSize() throws InterruptedException {
        ThreadPool pool = new ThreadPool(2);
        assertEquals(0, pool.getQueueSize());

        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = counter::incrementAndGet;

        pool.execute(task);
        pool.execute(task);
        Thread.sleep(100);
        assertEquals(2, counter.get());
        assertEquals(0, pool.getQueueSize());
    }

    @Test
    void testQueueSizeWhenTasksWaiting() {
        ThreadPool pool = new ThreadPool(1);
        pool.execute(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        });
        pool.execute(() -> {});
        assertEquals(1, pool.getQueueSize());
    }
}