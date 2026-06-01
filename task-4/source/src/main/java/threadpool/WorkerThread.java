package threadpool;

import staff.WorkTask;
import java.util.Queue;

public class WorkerThread extends Thread {
    private final Queue<WorkTask> taskQueue;

    public WorkerThread(Queue<WorkTask> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            WorkTask task;
            synchronized (taskQueue) {
                while (taskQueue.isEmpty()) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                        interrupt();
                        return;
                    }
                }
                task = taskQueue.poll();
                taskQueue.notifyAll();
            }
            if (task != null) {
                task.execute();
            }
        }
    }

    public void stopWorker() {
        interrupt();
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
    }
}