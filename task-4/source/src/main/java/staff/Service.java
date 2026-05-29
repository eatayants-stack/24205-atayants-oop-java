package staff;

import factory.AutoStorage;
import factory.FactoryStat;
import threadpool.ThreadPool;

public class Service implements Runnable {
    private final AutoStorage autoStorage;
    private final ThreadPool workerPool;
    private final AssemblyTaskFactory assemblyTaskFactory;
    private final FactoryStat stat;

    public Service(AutoStorage autoStorage, ThreadPool workerPool,
                   AssemblyTaskFactory assemblyTaskFactory, FactoryStat stat) {
        this.autoStorage = autoStorage;
        this.workerPool = workerPool;
        this.assemblyTaskFactory = assemblyTaskFactory;
        this.stat = stat;
    }

    @Override
    public void run() {
        try {
            autoStorage.notifyController();
            while (!Thread.currentThread().isInterrupted()) {
                autoStorage.waitForControllerNotification();

                int needed = autoStorage.checkoutNeededTasks();

                for (int i = 0; i < needed; i++) {
                    workerPool.execute(assemblyTaskFactory.createTask());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}