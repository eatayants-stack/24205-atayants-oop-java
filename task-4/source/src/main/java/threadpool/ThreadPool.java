package threadpool;

import staff.WorkTask;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPool {
    private final Queue<WorkTask> taskQueue;
    private final List<WorkerThread> workers;
    private final int taskQueueCapacity;

    public ThreadPool(int threadCount, int taskQueueCapacity) {
        this.taskQueue = new LinkedList<>();
        this.taskQueueCapacity = taskQueueCapacity;
        this.workers = new LinkedList<>();
        for (int i = 0; i < threadCount; i++) {
            workers.add(new WorkerThread(taskQueue));
        }
    }

    public void addTask(WorkTask workTask) {
        synchronized (taskQueue) {
            while (taskQueue.size() >= taskQueueCapacity) {
                try {
                    taskQueue.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            taskQueue.add(workTask);
            taskQueue.notifyAll();
        }
    }

    public void start() {
        for (WorkerThread w : workers) {
            w.start();
        }
    }

    public void stop() {
        for (WorkerThread w : workers) {
            w.stopWorker();
        }
    }

    public int getTaskQueueSize() {
        synchronized (taskQueue) {
            return taskQueue.size();
        }
    }
}