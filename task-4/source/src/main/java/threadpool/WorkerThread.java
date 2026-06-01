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
            WorkTask task = null;
            synchronized (taskQueue) {
                while (taskQueue.isEmpty()) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                        interrupt();    // восстанавливаем статус прерывания
                        return;         // выходим из потока
                    }
                }
                task = taskQueue.poll();
                // Уведомляем поток, который мог ждать в addTask (если очередь была полна)
                taskQueue.notifyAll();
            }
            // Выполняем задачу вне синхронизации, чтобы не блокировать очередь
            if (task != null) {
                task.execute();
            }
        }
    }

    public void stopWorker() {
        interrupt();
        // Дополнительно пробуждаем поток, если он ждёт на taskQueue.wait()
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
    }
}