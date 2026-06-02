package GUI;

import factory.FactoryStatistics;


public interface FactoryController {
    void start();
    FactoryStatistics getFactoryStatistics();
    void setBodySupplierDelay(int delay);
    void setMotorSupplierDelay(int delay);
    void setAccessorySupplierDelay(int delay);
    void setDealerDelay(int delay);
    void stop();
}