import org.junit.jupiter.api.Test;
import staff.WorkTask;
import threadpool.ThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {

    @Test
    void testExecuteTasks() throws Exception {

        ThreadPool pool = new ThreadPool(2, 10);

        AtomicInteger counter = new AtomicInteger();

        WorkTask task = counter::incrementAndGet;

        pool.start();

        pool.addTask(task);
        pool.addTask(task);

        Thread.sleep(200);

        pool.stop();

        assertEquals(2, counter.get());
        assertEquals(0, pool.getTaskQueueSize());
    }

    @Test
    void testQueueSizeWhenTaskWaiting() throws Exception {

        ThreadPool pool = new ThreadPool(1, 10);

        pool.start();

        WorkTask longTask = () -> {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException ignored) {
            }
        };

        pool.addTask(longTask);

        pool.addTask(() -> {});

        Thread.sleep(50);

        assertEquals(1, pool.getTaskQueueSize());

        pool.stop();
    }
}