import GUI.GUI;
import detail.*;   // исправлен импорт (было details.*)
import factory.*;
import staff.*;
import threadpool.ThreadPool;
import utilities.Configuration;
import GUI.CarProductionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.DetailType;

import javax.swing.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Configuration config = new Configuration("config.txt");

        Storage<Body> bodyStorage = new Storage<>(config.bodyStorageSize);
        Storage<Motor> motorStorage = new Storage<>(config.motorStorageSize);
        Storage<Accessory> accessoryStorage = new Storage<>(config.accessoryStorageSize);
        AutoStorage autoStorage = new AutoStorage(config.autoStorageSize);

        FactoryStat stat = new FactoryStat();
        ThreadPool workerPool = new ThreadPool(config.workers);
        AssemblyTaskFactory assemblyTaskFactory = new AssemblyTaskFactory(bodyStorage, motorStorage, accessoryStorage, autoStorage, stat);

        java.util.List<Supplier> bodySuppliers = new java.util.ArrayList<>();
        java.util.List<Supplier> motorSuppliers = new java.util.ArrayList<>();
        java.util.List<Supplier> accessorySuppliers = new java.util.ArrayList<>();

        for (int i = 0; i < config.bodySuppliers; i++) {
            Supplier s = new Supplier(bodyStorage, DetailType.BODY, config.bodySupplierDelay);
            bodySuppliers.add(s);
            new Thread(s, "BodySupplier-" + i).start();
        }
        for (int i = 0; i < config.motorSuppliers; i++) {
            Supplier s = new Supplier(motorStorage, DetailType.MOTOR, config.motorSupplierDelay);
            motorSuppliers.add(s);
            new Thread(s, "MotorSupplier-" + i).start();
        }
        for (int i = 0; i < config.accessorySuppliers; i++) {
            Supplier s = new Supplier(accessoryStorage, DetailType.ACCESSORY, config.accessorySupplierDelay);
            accessorySuppliers.add(s);
            new Thread(s, "AccessorySupplier-" + i).start();
        }

        for (int i = 1; i <= config.dealers; i++) {
            Dealer dealer = new Dealer(autoStorage, config.dealerDelay, i, config.logSale);
            new Thread(dealer, "Dealer-" + i).start();
        }

        Service service = new Service(autoStorage, workerPool, assemblyTaskFactory, stat);
        Thread serviceThread = new Thread(service, "Service");
        serviceThread.setDaemon(true);
        serviceThread.start();

        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI(workerPool,
                    config.bodySupplierDelay, config.motorSupplierDelay, config.accessorySupplierDelay);
            gui.getControlPanel().bindSuppliers(bodySuppliers, motorSuppliers, accessorySuppliers);
            CarProductionObserver listener = new CarProductionObserver(stat, gui.getControlPanel());
            autoStorage.addObserver(listener);
        });

        logger.info("Factory started");
    }
}