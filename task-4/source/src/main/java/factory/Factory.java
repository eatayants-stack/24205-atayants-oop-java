package factory;

import staff.*;
import GUI.FactoryController;
import product.*;
import utilities.Configuration;
import utilities.IDGenerator;
import java.util.ArrayList;
import java.util.List;


public class Factory implements FactoryController {
    private final Configuration config;

    private Storage<Body> bodyStorage;
    private Storage<Motor> motorStorage;
    private Storage<Accessory> accessoryStorage;
    private VehicleStorage carStorage;

    private List<PartSupplier<Body>> bodyPartSuppliers;
    private List<PartSupplier<Motor>> motorPartSuppliers;
    private List<PartSupplier<Accessory>> accessoryPartSuppliers;
    private List<Dealer> dealers;
    private Controller controller;
    private AssemblyLine assemblyLine;
    private final List<Thread> allThreads = new ArrayList<>();

    public Factory(Configuration config) {
        this.config = config;
        initStorages();
        initSuppliers();
        initDealers();
        initAssemblyLine();
        initController();
    }

    private void initStorages() {
        bodyStorage = new Storage<>(config.bodyStorageSize);
        motorStorage = new Storage<>(config.motorStorageSize);
        accessoryStorage = new Storage<>(config.accessoryStorageSize);
        carStorage = new VehicleStorage(config.carStorageSize);
    }



    private void initSuppliers() {
        bodyPartSuppliers = new ArrayList<>();
        for (int i = 0; i < config.bodySuppliers; i++) {
            bodyPartSuppliers.add(new PartSupplier<>(bodyStorage,
                    () -> new Body(IDGenerator.INSTANCE.nextBodyId()),
                    config.bodySupplierDelay));
        }
        motorPartSuppliers = new ArrayList<>();
        for (int i = 0; i < config.motorSuppliers; i++) {
            motorPartSuppliers.add(new PartSupplier<>(motorStorage,
                    () -> new Motor(IDGenerator.INSTANCE.nextMotorId()),
                    config.motorSupplierDelay));
        }
        accessoryPartSuppliers = new ArrayList<>();
        for (int i = 0; i < config.accessorySuppliers; i++) {
            accessoryPartSuppliers.add(new PartSupplier<>(accessoryStorage,
                    () -> new Accessory(IDGenerator.INSTANCE.nextAccessoryId()),
                    config.accessorySupplierDelay));
        }
    }

    private void initDealers() {
        dealers = new ArrayList<>();
        for (int i = 0; i < config.dealers; i++) {
            dealers.add(new Dealer(carStorage, config.dealerDelay, i + 1, config.logSale));
        }
    }

    private void initAssemblyLine() {
        int workers = config.workers;
        int queueSize = workers * 2;
        assemblyLine = new AssemblyLine(workers, queueSize,
                bodyStorage, motorStorage, accessoryStorage, carStorage);
    }

    private void initController() {
        controller = new Controller(assemblyLine, carStorage);
        carStorage.register(controller);
    }

    private void startThread(Runnable r) {
        Thread t = new Thread(r);
        allThreads.add(t);
        t.start();
    }

    @Override
    public void start() {

        for (PartSupplier<Body> s : bodyPartSuppliers)
            startThread(s);
        for (PartSupplier<Motor> s : motorPartSuppliers)
            startThread(s);
        for (PartSupplier<Accessory> s : accessoryPartSuppliers)
            startThread(s);

        assemblyLine.start();

        controller.initialize();

        for (Dealer d : dealers)
            startThread(d);
    }

    @Override
    public FactoryStatistics getFactoryStatistics() {
        return new FactoryStatistics(
                assemblyLine.getPendingOrders(),
                bodyStorage.getCurrentSize(), bodyStorage.getTotalProduced(),
                motorStorage.getCurrentSize(), motorStorage.getTotalProduced(),
                accessoryStorage.getCurrentSize(), accessoryStorage.getTotalProduced(),
                carStorage.getCurrentSize(), carStorage.getTotalProduced()
        );
    }

    @Override
    public void setBodySupplierDelay(int delay) {
        bodyPartSuppliers.forEach(s -> s.setDelay(delay));
    }

    @Override
    public void setMotorSupplierDelay(int delay) {
        motorPartSuppliers.forEach(s -> s.setDelay(delay));
    }

    @Override
    public void setAccessorySupplierDelay(int delay) {
        accessoryPartSuppliers.forEach(s -> s.setDelay(delay));
    }

    @Override
    public void setDealerDelay(int delay) {
        dealers.forEach(d -> d.setDelay(delay));
    }

    @Override
    public void stop() {
        allThreads.forEach(Thread::interrupt);
        assemblyLine.stop();
    }
}