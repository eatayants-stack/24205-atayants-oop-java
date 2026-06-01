package staff;

import factory.Storage;
import product.Product;

public class PartSupplier<T extends Product>
        extends DelayService
        implements Runnable {

    private final Storage<T> storage;
    private final ProductFactory<T> creator;

    public PartSupplier(
            Storage<T> storage,
            ProductFactory<T> creator,
            int delayMs) {

        super(delayMs);
        this.storage = storage;
        this.creator = creator;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                storage.put(creator.create());
                Thread.sleep(getDelay());
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}