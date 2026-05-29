package staff;

import factory.AutoStorage;
import factory.Storage;
import detail.*;
import factory.FactoryStat;

public class AssemblyTaskFactory {
    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final AutoStorage autoStorage;
    private final FactoryStat stat;

    public AssemblyTaskFactory(Storage<Body> bodyStorage, Storage<Motor> motorStorage,
                               Storage<Accessory> accessoryStorage, AutoStorage autoStorage,
                               FactoryStat stat) {
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;
        this.stat = stat;
    }

    public Runnable createTask() {
        return new CarBuilder(bodyStorage, motorStorage, accessoryStorage, autoStorage, stat);
    }
}