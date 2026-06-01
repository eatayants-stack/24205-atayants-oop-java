package staff;

import factory.Event;
import factory.VehicleStorage;
import factory.Observer;

public class Controller implements Observer {
    private final AssemblyLine assemblyLine;
    private final VehicleStorage carStorage;
    private final int capacity;

    public Controller(AssemblyLine assemblyLine, VehicleStorage carStorage) {
        this.assemblyLine = assemblyLine;
        this.carStorage = carStorage;
        this.capacity = carStorage.getCapacity();
    }

    public void initialize() {
        int capacity = carStorage.getCapacity();

        for (int i = 0; i < capacity; i++) {
            assemblyLine.assembleCar();
        }
    }

    private void onCarDemand() {
        int carsInStock = carStorage.getCurrentSize();
        int pendingTasks = assemblyLine.getPendingOrders();
        int required = capacity - (carsInStock + pendingTasks);
        for (int i = 0; i < required; i++) {
            assemblyLine.assembleCar();
        }
    }

    @Override
    public void update(Event event) {
        if (event == Event.CAR_DEMAND) {
            onCarDemand();
        }
    }
}