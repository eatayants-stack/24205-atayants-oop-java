package threadpool;

import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);
    private final BlockingQueue<Runnable> taskQueue;
    private final int id;

    public WorkerThread(BlockingQueue<Runnable> taskQueue, int id) {
        this.taskQueue = taskQueue;
        this.id = id;
        setName("PooledThread-" + id);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Runnable task = taskQueue.take();
                try {
                    task.run();
                } catch (Throwable t) {
                    logger.error("Uncaught exception in task executed by {}", getName(), t);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}