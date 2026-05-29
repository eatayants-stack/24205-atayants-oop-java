package threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final WorkerThread[] workers;
    private final boolean isRunning = true;

    public ThreadPool(int poolSize) {
        taskQueue = new LinkedBlockingQueue<>();
        workers = new WorkerThread[poolSize];
        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread(taskQueue, i);
            workers[i].start();
        }
    }

    public void execute(Runnable task) {
        if (!isRunning) throw new IllegalStateException("ThreadPool is stopped");
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to queue task due to interruption", e);
        }
    }

    public int getQueueSize() { return taskQueue.size(); }

}