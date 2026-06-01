package threadpool;

import staff.WorkTask;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPool {
    private final Queue<WorkTask> taskQueue;       // очередь задач
    private final List<WorkerThread> workers;
    private final int taskQueueCapacity;           // ограничение размера очереди (опционально)

    public ThreadPool(int threadCount, int taskQueueCapacity) {
        this.taskQueue = new LinkedList<>();
        this.taskQueueCapacity = taskQueueCapacity;
        this.workers = new LinkedList<>();
        for (int i = 0; i < threadCount; i++) {
            workers.add(new WorkerThread(taskQueue));
        }
    }

    /**
     * Добавить задачу в очередь.
     * Блокируется, если очередь заполнена (аналог put).
     */
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
            taskQueue.notifyAll();   // пробуждаем ожидающих рабочих
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

    public int getThreadCount() {
        return workers.size();
    }
}